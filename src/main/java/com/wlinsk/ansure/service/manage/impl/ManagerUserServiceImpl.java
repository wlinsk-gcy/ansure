package com.wlinsk.ansure.service.manage.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlinsk.ansure.basic.config.redis.RedisUtils;
import com.wlinsk.ansure.basic.enums.DelStateEnum;
import com.wlinsk.ansure.basic.enums.UserRoleEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.mapper.UserMapper;
import com.wlinsk.ansure.model.bo.TokenBo;
import com.wlinsk.ansure.model.dto.user.req.ManagerUpdateRoleReqDTO;
import com.wlinsk.ansure.model.dto.user.req.ManagerUserQueryPageReqDTO;
import com.wlinsk.ansure.model.dto.user.resp.ManagerUserQueryPageRespDTO;
import com.wlinsk.ansure.model.entity.User;
import com.wlinsk.ansure.service.manage.ManagerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author: wlinsk
 * @Date: 2024/5/26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerUserServiceImpl implements ManagerUserService {
    private final UserMapper userMapper;
    private final BasicTransactionTemplate basicTransactionTemplate;
    private final RedisUtils redisUtils;
    @Override
    public IPage<ManagerUserQueryPageRespDTO> queryPage(ManagerUserQueryPageReqDTO reqDTO) {
        Page<User> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        IPage<User> iPage = userMapper.queryUserPage(page, reqDTO.getUserId(), reqDTO.getUserAccount(),
                reqDTO.getUserName(), reqDTO.getUserRole());
        return iPage.convert(user -> {
            ManagerUserQueryPageRespDTO dto = new ManagerUserQueryPageRespDTO();
            BeanUtils.copyProperties(user, dto);
            return dto;
        });
    }

    @Override
    public void deleteById(String userId) {
        User targetUser = userMapper.queryByUserId(userId);
        Optional.ofNullable(targetUser).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        User user = userMapper.queryByUserId(BasicAuthContextUtils.getUserId());
        if (Objects.isNull(user) || !Objects.equals(user.getUserRole(), UserRoleEnum.ADMIN)) {
            throw new BasicException(SysCode.SYSTEM_NO_PERMISSION);
        }
        User delete = new User();
        delete.setUserId(targetUser.getUserId());
        delete.setVersion(targetUser.getVersion());
        delete.setUpdateTime(new Date());
        delete.setDelState(DelStateEnum.DEL);
        delete.setUserAccount(String.format("%s_##",targetUser.getUserAccount()));
        basicTransactionTemplate.execute(action -> {
            if (userMapper.deleteUser(delete) != 1){
                throw new BasicException(SysCode.DATABASE_DELETE_ERROR);
            }
            String val = redisUtils.getVal(targetUser.getUserId());
            if (StringUtils.isNotBlank(val)){
                redisUtils.delVal(val);
            }
            return SysCode.success;
        });


    }

    @Override
    public void updateRole(ManagerUpdateRoleReqDTO reqDTO) {
        User user = userMapper.queryByUserId(reqDTO.getUserId());
        if (Objects.isNull(user)){
            throw new BasicException(SysCode.DATA_NOT_FOUND);
        }
        User updateUser = new User();
        updateUser.setUserId(reqDTO.getUserId());
        updateUser.setUserRole(reqDTO.getUserRole());
        updateUser.setVersion(user.getVersion());
        updateUser.setUpdateTime(new Date());

        basicTransactionTemplate.execute(action -> {
            int result = userMapper.updateUserRole(updateUser);
            if (result != 1){
                throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
            }
            String oldTokenBo = redisUtils.getVal(reqDTO.getUserId());
            if (StringUtils.isNotBlank(oldTokenBo)){
                TokenBo tokenBo = JSON.parseObject(oldTokenBo, TokenBo.class);
                redisUtils.delVal(tokenBo.getToken());
                redisUtils.delVal(reqDTO.getUserId());
            }
            return SysCode.success;
        });
    }
}
