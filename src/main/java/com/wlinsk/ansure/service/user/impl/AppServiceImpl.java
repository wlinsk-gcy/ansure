package com.wlinsk.ansure.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlinsk.ansure.basic.enums.*;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.id_generator.IdUtils;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.mapper.AppMapper;
import com.wlinsk.ansure.mapper.UserMapper;
import com.wlinsk.ansure.model.dto.app.req.*;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppDetailsRespDTO;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppListRespDTO;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppPageRespDTO;
import com.wlinsk.ansure.model.dto.user.resp.QueryUserDetailRespDTO;
import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.User;
import com.wlinsk.ansure.service.user.AppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppServiceImpl extends ServiceImpl<AppMapper, App>
    implements AppService {

    private final AppMapper appMapper;
    private final UserMapper userMapper;
    private final BasicTransactionTemplate basicTransactionTemplate;

    @Override
    public String addApp(AddAppReqDTO dto) {
        if (AppTypeEnum.TEST.equals(dto.getAppType())){
            throw new BasicException(SysCode.NOT_SUPPORT_TEST_APP);
        }
        if (ScoringStrategyEnum.AI_SCORE.equals(dto.getScoringStrategy())){
            throw new BasicException(SysCode.NOT_SUPPORT_AI_SCORE);
        }
        String userId = BasicAuthContextUtils.getUserId();
        User user = userMapper.queryByUserId(userId);
        Optional.ofNullable(user)
                .orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        App app = new App();
        app.init();
        app.setAppId(IdUtils.build(null));
        BeanUtils.copyProperties(dto,app);
        app.setUserId(userId);
        app.setReviewStatus(ReviewStatusEnum.TO_BE_REVIEWED);
        basicTransactionTemplate.execute(action -> {
            if (appMapper.insert(app) != 1){
                throw new BasicException(SysCode.DATABASE_INSERT_ERROR);
            }
            return SysCode.success;
        });
        return app.getAppId();
    }

    @Override
    public void deleteApp(DeleteAppReqDTO reqDTO) {
        String userId = BasicAuthContextUtils.getUserId();
        User user = userMapper.queryByUserId(userId);
        Optional.ofNullable(user).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        App app = appMapper.queryByAppId(reqDTO.getAppId());
        Optional.ofNullable(app).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        if (!UserRoleEnum.ADMIN.equals(user.getUserRole()) && !userId.equals(app.getUserId())){
            log.error("无法删除不属于自己的应用");
            throw new BasicException(SysCode.DATA_NOT_FOUND);
        }
        App deleteApp = new App();
        deleteApp.setUpdateTime(new Date());
        deleteApp.setVersion(app.getVersion());
        deleteApp.setDelState(DelStateEnum.DEL);
        deleteApp.setAppId(app.getAppId());
        basicTransactionTemplate.execute(action -> {
            if (appMapper.deleteByAppId(deleteApp) != 1){
                throw new BasicException(SysCode.DATABASE_DELETE_ERROR);
            }
            return SysCode.success;
        });
    }

    @Override
    public IPage<QueryAppPageRespDTO> queryPage(QueryAppPageReqDTO reqDTO) {
        Page<App> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        IPage<App> iPage = appMapper.queryPageForClient(page,reqDTO.getContext());
        return buildQueryPageResult(iPage);
    }

    @Override
    public QueryAppDetailsRespDTO queryById(String appId) {
        App app = appMapper.queryByAppId(appId);
        Optional.ofNullable(app).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        User user = userMapper.queryByUserId(app.getUserId());
        QueryAppDetailsRespDTO respDTO = new QueryAppDetailsRespDTO();
        BeanUtils.copyProperties(app,respDTO);
        if (Objects.nonNull(user)){
            QueryUserDetailRespDTO userDTO = new QueryUserDetailRespDTO();
            BeanUtils.copyProperties(user,userDTO);
            respDTO.setUserInfo(userDTO);
        }
        return respDTO;
    }

    @Override
    public void updateApp(UpdateAppReqDTO reqDTO) {
        if (AppTypeEnum.TEST.equals(reqDTO.getAppType())){
            throw new BasicException(SysCode.NOT_SUPPORT_TEST_APP);
        }
        if (ScoringStrategyEnum.AI_SCORE.equals(reqDTO.getScoringStrategy())){
            throw new BasicException(SysCode.NOT_SUPPORT_AI_SCORE);
        }
        App app = appMapper.queryByAppId(reqDTO.getAppId());
        Optional.ofNullable(app).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        String currentUserId = BasicAuthContextUtils.getUserId();
        User currentUser = userMapper.queryByUserId(currentUserId);
        if (!app.getUserId().equals(currentUserId) || !UserRoleEnum.ADMIN.equals(currentUser.getUserRole())){
            log.warn("无法修改不属于自己的应用");
            throw new BasicException(SysCode.DATA_NOT_FOUND);
        }
        App update = new App();
        BeanUtils.copyProperties(reqDTO,update);
        update.setVersion(app.getVersion());
        update.setUpdateTime(new Date());
        update.setAppId(reqDTO.getAppId());
        update.setReviewStatus(ReviewStatusEnum.TO_BE_REVIEWED);
        basicTransactionTemplate.execute(action -> {
            if (appMapper.updateApp(update) != 1){
                throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
            }
          return SysCode.success;
        });
    }

    @Override
    public IPage<QueryAppPageRespDTO> queryMyPage(QueryMyAppPageReqDTO reqDTO) {
        Page<App> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        String userId = BasicAuthContextUtils.getUserId();
        IPage<App> iPage = appMapper.queryMyPageForClient(page,userId,reqDTO.getAppId(),reqDTO.getAppName());
        return buildQueryPageResult(iPage);
    }

    @Override
    public List<QueryAppListRespDTO> queryList(QueryAppListReqDTO reqDTO) {
        List<App> apps = appMapper.queryFiveRecords(reqDTO.getAppName());
        if (CollectionUtils.isEmpty(apps)){
            return List.of();
        }
        return apps.stream().map(app -> {
            QueryAppListRespDTO respDTO = new QueryAppListRespDTO();
            BeanUtils.copyProperties(app, respDTO);
            respDTO.setAppType(app.getAppType().getMessage());
            respDTO.setScoringStrategy(app.getScoringStrategy().getMessage());
            respDTO.setReviewStatus(app.getReviewStatus().getMessage());
            return respDTO;
        }).toList();
    }

    @Override
    public QueryAppListRespDTO queryLast() {
        App app = appMapper.queryLast();
        if (Objects.isNull(app)){
            return new QueryAppListRespDTO();
        }
        QueryAppListRespDTO respDTO = new QueryAppListRespDTO();
        BeanUtils.copyProperties(app, respDTO);
        respDTO.setAppType(app.getAppType().getMessage());
        respDTO.setScoringStrategy(app.getScoringStrategy().getMessage());
        respDTO.setReviewStatus(app.getReviewStatus().getMessage());
        return respDTO;
    }

    private IPage<QueryAppPageRespDTO> buildQueryPageResult(IPage<App> iPage){
        if (CollectionUtils.isEmpty(iPage.getRecords())){
            return iPage.convert(app -> null);
        }
        List<String> collect = iPage.getRecords().stream().map(App::getUserId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)){
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
        Map<String, User> userMap = userMapper.queryByUserIdList(collect).stream().collect(Collectors.toMap(User::getUserId, Function.identity()));
        return iPage.convert(app -> {
            QueryAppPageRespDTO respDTO = new QueryAppPageRespDTO();
            BeanUtils.copyProperties(app, respDTO);
            User user = userMap.get(app.getUserId());
            if (Objects.nonNull(user)){
                QueryUserDetailRespDTO userDTO = new QueryUserDetailRespDTO();
                BeanUtils.copyProperties(user, userDTO);
                respDTO.setUserInfo(userDTO);
            }
            return respDTO;
        });
    }
}




