package com.wlinsk.ansure.service.user.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlinsk.ansure.basic.config.email.EmailClient;
import com.wlinsk.ansure.basic.config.redis.RedisUtils;
import com.wlinsk.ansure.basic.config.redisson.RedissonConstant;
import com.wlinsk.ansure.basic.enums.ThreePartLoginEnums;
import com.wlinsk.ansure.basic.enums.UserRoleEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.id_generator.IdUtils;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.basic.utils.PasswordUtils;
import com.wlinsk.ansure.basic.utils.ThreePartLoginUtils;
import com.wlinsk.ansure.basic.utils.VerifyCodeUtils;
import com.wlinsk.ansure.mapper.UserMapper;
import com.wlinsk.ansure.model.bo.GiteeLoginCallbackBo;
import com.wlinsk.ansure.model.bo.GiteeUserInfoBo;
import com.wlinsk.ansure.model.bo.TokenBo;
import com.wlinsk.ansure.model.constant.Constant;
import com.wlinsk.ansure.model.dto.user.req.*;
import com.wlinsk.ansure.model.dto.user.resp.QueryUserDetailRespDTO;
import com.wlinsk.ansure.model.dto.user.resp.UserLoginRespDTO;
import com.wlinsk.ansure.model.entity.User;
import com.wlinsk.ansure.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    private final String defaultAvatar = "https://wlinsk-ai-aq.oss-cn-guangzhou.aliyuncs.com/2024/05/26/73603796218521296026548255599116.png";
    private final UserMapper userMapper;
    private final RedisUtils redisUtils;
    private final BasicTransactionTemplate basicTransactionTemplate;
    private final ThreePartLoginUtils threePartLoginUtils;
    private final RedissonClient redissonClient;
    private final EmailClient emailClient;

    @Override
    public void register(UserRegisterReqDTO dto) {
        String code_key = String.format("%s:%s:%s", Constant.Redis.VERIFY_CODE, dto.getUserAccount(), dto.getVerifyCode());
        String code = redisUtils.getVal(code_key);
        if (StringUtils.isBlank(code)){
            throw new BasicException(SysCode.UNKNOW_VERIFY_CODE);
        }
        RLock lock = redissonClient.getLock(RedissonConstant.REGISTER_LOCK + dto.getUserAccount());
        try {
            lock.lock();
            if (Objects.nonNull(userMapper.queryByUserAccount(dto.getUserAccount()))){
                throw new BasicException(SysCode.USER_ACCOUNT_ALREADY_EXIST);
            }
            if (!dto.getUserPassword().equals(dto.getCheckPassword())){
                throw new BasicException(SysCode.USER_PASSWORD_ERROR);
            }
            String encodePassword = PasswordUtils.encode(dto.getUserPassword());
            registerUserToDB(dto.getUserAccount(), dto.getUserAccount(),encodePassword);
        } catch (Exception e) {
            log.error("register error", e);
            if (e instanceof BasicException){
                throw e;
            }
            throw new BasicException(SysCode.SYSTEM_ERROE);
        } finally {
            if (Objects.nonNull(lock) && lock.isLocked()){
                lock.unlock();
            }
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO dto) {
        RLock lock = redissonClient.getLock(RedissonConstant.LOGIN_LOCK + dto.getUserAccount());
        try {
            lock.lock();
            User user = userMapper.queryByUserAccount(dto.getUserAccount());
            Optional.ofNullable(user)
                    .orElseThrow(() -> new BasicException(SysCode.USER_ACCOUNT_NOT_EXIST));
            if (!PasswordUtils.matches(dto.getUserPassword(),user.getUserPassword())){
                throw new BasicException(SysCode.USER_ACCOUNT_PASSWORD_ERROR);
            }
            if (UserRoleEnum.BAN.equals(user.getUserRole())){
                throw new BasicException(SysCode.USER_DISABLED);
            }
            return buildLoginResult(user,null);
        } catch (Exception e) {
            log.error("login error", e);
            if (e instanceof BasicException){
                throw e;
            }
            throw new BasicException(SysCode.SYSTEM_ERROE);
        } finally {
            if (Objects.nonNull(lock) && lock.isLocked()){
                lock.unlock();
            }
        }
    }

    @Override
    public QueryUserDetailRespDTO queryById(String userId) {
        User user = userMapper.queryByUserId(userId);
        Optional.ofNullable(user)
                .orElseThrow(() -> new BasicException(SysCode.USER_ACCOUNT_NOT_EXIST));
        QueryUserDetailRespDTO result = new QueryUserDetailRespDTO();
        BeanUtils.copyProperties(user,result);
        return result;
    }

    @Override
    public void logout() {
        String userId = BasicAuthContextUtils.getUserId();
        if (StringUtils.isBlank(userId)){
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
        String oldTokenBo = redisUtils.getVal(userId);
        if (StringUtils.isBlank(oldTokenBo)){
            //未登录
            throw new BasicException(SysCode.SYS_TOKEN_EXPIRE);
        }
        TokenBo tokenBo = JSON.parseObject(oldTokenBo, TokenBo.class);
        redisUtils.delVal(tokenBo.getToken());
        redisUtils.delVal(userId);
    }

    @Override
    public String threePartLogin(ThreePartLoginReqDTO dto) {
        String result = threePartLoginUtils.doAuthorize(dto.getLoginType());
        Optional.ofNullable(result).orElseThrow(() -> new BasicException(SysCode.THREE_PART_LOGIN_ERROR));
        return result;
    }

    @Override
    public UserLoginRespDTO threePartLoginCallback(String code) {
        GiteeLoginCallbackBo callbackResult = threePartLoginUtils.getToken(code, null);
        GiteeUserInfoBo userInfo = threePartLoginUtils.getUserInfo(callbackResult.getAccess_token());
        String userAccount = String.format("%s_%d_%s", ThreePartLoginEnums.GITEE.getCode(),userInfo.getId(),userInfo.getName());
        User user = userMapper.queryByUserAccount(userAccount);
        if (Objects.isNull(user)){
            //注册
            String build = IdUtils.build(null);
            int length = build.length();
            String password = "gitee" + build.substring(length-8,length);
            String encodePassword = PasswordUtils.encode(password);
            user = registerUserToDB(userAccount,userInfo.getName(),encodePassword);
        }
        if (UserRoleEnum.BAN.equals(user.getUserRole())){
            throw new BasicException(SysCode.USER_DISABLED);
        }
        return buildLoginResult(user,callbackResult);
    }

    @Override
    public void updatePassword(UpdatePasswordReqDTO dto) {
        String userId = BasicAuthContextUtils.getUserId();
        User user = userMapper.queryByUserId(userId);
        if (!PasswordUtils.matches(dto.getOriginalPassword(),user.getUserPassword())){
            throw new BasicException(SysCode.USER_PASSWORD_ERROR);
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())){
            throw new BasicException(SysCode.USER_PASSWORD_NOT_SAME);
        }
        User update = new User();
        update.setUserId(userId);
        update.setUpdateTime(new Date());
        update.setUserPassword(dto.getNewPassword());
        update.setVersion(user.getVersion());
        basicTransactionTemplate.execute(action -> {
            int result = userMapper.updatePassword(update);
            if (result != 1){
                throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
            }
            return SysCode.success;
        });
    }

    @Override
    public void updateUserName(UpdateUserNameReqDTO dto) {
        String userId = BasicAuthContextUtils.getUserId();
        User user = userMapper.queryByUserId(userId);
        User update = new User();
        update.setUserId(userId);
        update.setUpdateTime(new Date());
        update.setUserName(dto.getUserName());
        update.setVersion(user.getVersion());
        basicTransactionTemplate.execute(action -> {
            int result = userMapper.updateUserName(update);
            if (result != 1){
                throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
            }
            return SysCode.success;
        });
    }

    @Override
    public void updateUserProfile(UpdateUserProfileReqDTO dto) {
        String userId = BasicAuthContextUtils.getUserId();
        User user = userMapper.queryByUserId(userId);
        if (StringUtils.isNotBlank(dto.getUserProfile()) && dto.getUserProfile().length() > 256){
            throw new BasicException(SysCode.USER_PROFILE_LENGTH_ERROR);
        }
        User update = new User();
        update.setUserId(userId);
        update.setUpdateTime(new Date());
        update.setUserProfile(dto.getUserProfile());
        update.setVersion(user.getVersion());
        basicTransactionTemplate.execute(action -> {
            int result = userMapper.updateUserProfile(update);
            if (result != 1){
                throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
            }
            return SysCode.success;
        });
    }

    @Override
    public void reduceAiPoint(String userId) {
        RLock lock = redissonClient.getLock(RedissonConstant.REDUCE_AI_POINT_LOCK + userId);
        try {
            lock.lock();
            User user = userMapper.queryByUserId(userId);
            Optional.ofNullable(user)
                    .orElseThrow(() -> new BasicException(SysCode.USER_ACCOUNT_NOT_EXIST));
            if (user.getAiPoint() <= 0){
                throw new BasicException(SysCode.USER_AI_POINT_NOT_ENOUGH);
            }
            basicTransactionTemplate.execute(action -> {
                int result = userMapper.reduceAiPoint(user.getAiPoint(), userId, user.getVersion());
                if (result != 1){
                    throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
                }
                return SysCode.success;
            });
        } catch (Exception e) {
            log.error("reduceAiPoint error", e);
            if (e instanceof BasicException){
                throw e;
            }
            throw new BasicException(SysCode.SYSTEM_ERROE);
        } finally {
            if (Objects.nonNull(lock) && lock.isLocked()){
                lock.unlock();
            }
        }
    }

    @Override
    public int queryAiPoint(String userId) {
        User user = userMapper.queryByUserId(userId);
        Optional.ofNullable(user)
                .orElseThrow(() -> new BasicException(SysCode.USER_ACCOUNT_NOT_EXIST));
        return user.getAiPoint();
    }

    @Override
    public void verify(UserVerifyReqDTO reqDTO) {
        String time_key = String.format("%s:%s", Constant.Redis.VERIFY_TIME, reqDTO.getUserAccount());
        String lastSendTime = redisUtils.getVal(time_key);
        long now = System.currentTimeMillis();
        if (lastSendTime != null && (now - Long.parseLong(lastSendTime)) < 180 * 1000) {
            throw new BasicException(SysCode.SEND_VERIFY_CODE_TOO_OFTEN);
        }
        String code = VerifyCodeUtils.generateNumericCode(6);
        String code_key = String.format("%s:%s:%s", Constant.Redis.VERIFY_CODE, reqDTO.getUserAccount(), code);
        emailClient.sendVerifyCodeEmail(reqDTO.getUserAccount(),code);
        redisUtils.setVal(time_key, String.valueOf(System.currentTimeMillis()),180);
        redisUtils.setVal(code_key, code,180);
    }

    private User registerUserToDB(String userAccount,String userName,String userPassword){
        User user = new User();
        user.init();
        user.setUserId(IdUtils.build(null));
        user.setUserName(userName);
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        user.setUserAvatar(defaultAvatar);
        user.setUserRole(UserRoleEnum.USER);
        basicTransactionTemplate.execute(action -> {
            if (userMapper.insert(user) != 1){
                throw new BasicException(SysCode.USER_REGISTER_ERROR);
            }
            return SysCode.success;
        });
        return user;
    }

    private UserLoginRespDTO buildLoginResult(User user,GiteeLoginCallbackBo callbackBo){
        TokenBo tokenBo = new TokenBo();
        String token = UUID.randomUUID().toString();
        tokenBo.setToken(token);
        if (Objects.nonNull(callbackBo)){
            tokenBo.setThreePartAccessToken(callbackBo.getAccess_token());
            tokenBo.setThreePartRefreshToken(callbackBo.getRefresh_token());
        }
        String jsonTokenBo = JSON.toJSONString(tokenBo);
        String value = String.format("%s:%s",user.getUserId(),user.getUserRole().name());
        redisUtils.setVal(token,value,60*60*24);
        String oldJsonTokenBo = redisUtils.getVal(user.getUserId());
        if (StringUtils.isNotBlank(oldJsonTokenBo)){
            TokenBo oldTokenBo = JSON.parseObject(oldJsonTokenBo, TokenBo.class);
            redisUtils.delVal(oldTokenBo.getToken());
        }
        redisUtils.setVal(user.getUserId(),jsonTokenBo,60*60*24);
        UserLoginRespDTO result = new UserLoginRespDTO();
        result.setToken(token);
        result.setUserId(user.getUserId());
        return result;
    }
}




