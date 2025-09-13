package com.wlinsk.ansure.service.manage.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlinsk.ansure.basic.enums.AppTypeEnum;
import com.wlinsk.ansure.basic.enums.DelStateEnum;
import com.wlinsk.ansure.basic.enums.ScoringStrategyEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.basic.utils.BusinessValidatorUtils;
import com.wlinsk.ansure.mapper.AppMapper;
import com.wlinsk.ansure.mapper.QuestionMapper;
import com.wlinsk.ansure.mapper.ScoringResultMapper;
import com.wlinsk.ansure.mapper.UserMapper;
import com.wlinsk.ansure.model.dto.app.req.ManagerAppQueryPageReqDTO;
import com.wlinsk.ansure.model.dto.app.req.ManagerReviewAddReqDTO;
import com.wlinsk.ansure.model.dto.app.req.ManagerUpdateAppReqDTO;
import com.wlinsk.ansure.model.dto.app.resp.ManagerAppQueryPageRespDTO;
import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.Question;
import com.wlinsk.ansure.model.entity.ScoringResult;
import com.wlinsk.ansure.service.manage.ManagerAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerAppServiceImpl implements ManagerAppService {

    private final AppMapper appMapper;
    private final UserMapper userMapper;
    private final BasicTransactionTemplate basicTransactionTemplate;
    private final BusinessValidatorUtils businessValidatorUtils;
    private final QuestionMapper questionMapper;
    private final ScoringResultMapper scoringResultMapper;

    @Override
    public void updateApp(ManagerUpdateAppReqDTO reqDTO) {
        if (AppTypeEnum.TEST.equals(reqDTO.getAppType())){
            throw new BasicException(SysCode.NOT_SUPPORT_TEST_APP);
        }
        if (ScoringStrategyEnum.AI_SCORE.equals(reqDTO.getScoringStrategy())){
            throw new BasicException(SysCode.NOT_SUPPORT_AI_SCORE);
        }
        App app = appMapper.queryByAppId(reqDTO.getAppId());
        Optional.ofNullable(app).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        App update = new App();
        BeanUtils.copyProperties(reqDTO,update);
        Date updateTime = new Date();
        update.setUpdateTime(updateTime);
        update.setVersion(app.getVersion());
        update.setReviewerId(BasicAuthContextUtils.getUserId());
        update.setReviewTime(updateTime);
        basicTransactionTemplate.execute(action -> {
            int result = appMapper.updateApp(update);
            if (result != 1){
                throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
            }
            return SysCode.success;
        });

    }

    @Override
    public void reviewApp(ManagerReviewAddReqDTO reqDTO) {
        App app = appMapper.queryByAppId(reqDTO.getAppId());
        Optional.ofNullable(app).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        if (app.getReviewStatus().equals(reqDTO.getReviewStatus())){
            throw new BasicException(SysCode.APP_REVIEW_STATUS_HAS_CHANGED);
        }
        Question question = questionMapper.queryByAppId(app.getAppId());
        Optional.ofNullable(question).orElseThrow(() -> new BasicException(SysCode.QUESTION_NON_FOUND));
        List<ScoringResult> scoringResults = scoringResultMapper.queryByAppIdOrderByScoreRange(app.getAppId());
        if (CollectionUtils.isEmpty(scoringResults)){
            throw new BasicException(SysCode.SCORING_RESULT_NON_FOUND);
        }
        String userId = BasicAuthContextUtils.getUserId();
        Date updateTime = new Date();
        App updateApp = new App();
        updateApp.setAppId(app.getAppId());
        updateApp.setVersion(app.getVersion());
        updateApp.setReviewerId(userId);
        updateApp.setUpdateTime(updateTime);
        updateApp.setReviewTime(updateTime);
        updateApp.setReviewStatus(reqDTO.getReviewStatus());
        updateApp.setReviewMessage(reqDTO.getReviewMessage());
        basicTransactionTemplate.execute(action -> {
            if (appMapper.updateApp(updateApp) != 1){
                throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
            }
            return SysCode.success;
        });
    }

    @Override
    public IPage<ManagerAppQueryPageRespDTO> queryPage(ManagerAppQueryPageReqDTO req) {
        Page<App> page = new Page<>(req.getPageNum(), req.getPageSize());
        IPage<App> iPage = appMapper.queryPage(page,req.getAppId(),req.getAppName());
        return iPage.convert(app -> {
            ManagerAppQueryPageRespDTO respDTO = new ManagerAppQueryPageRespDTO();
            BeanUtils.copyProperties(app, respDTO);
            return respDTO;
        });
    }

    @Override
    public void deleteById(String appId) {
        App app = appMapper.queryByAppId(appId);
        Optional.ofNullable(app).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        businessValidatorUtils.validateUserInfo(app.getUserId());
        App delete = new App();
        delete.setAppId(app.getAppId());
        delete.setVersion(app.getVersion());
        delete.setUpdateTime(new Date());
        delete.setDelState(DelStateEnum.DEL);
        basicTransactionTemplate.execute(action -> {
            if (appMapper.deleteByAppId(delete) != 1){
                throw new BasicException(SysCode.DATABASE_DELETE_ERROR);
            }
            return SysCode.success;
        });
    }
}
