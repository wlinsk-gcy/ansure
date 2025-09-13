package com.wlinsk.ansure.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlinsk.ansure.basic.enums.AppTypeEnum;
import com.wlinsk.ansure.basic.enums.DelStateEnum;
import com.wlinsk.ansure.basic.enums.ReviewStatusEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.id_generator.IdUtils;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.basic.utils.BusinessValidatorUtils;
import com.wlinsk.ansure.mapper.AppMapper;
import com.wlinsk.ansure.mapper.ScoringResultMapper;
import com.wlinsk.ansure.mapper.UserMapper;
import com.wlinsk.ansure.model.dto.scoringResult.req.AddScoringResultReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.req.QueryScoringResultPageReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.req.UpdateScoringResultReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.resp.QueryScoringResultPageRespDTO;
import com.wlinsk.ansure.model.dto.user.resp.QueryUserDetailRespDTO;
import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.ScoringResult;
import com.wlinsk.ansure.model.entity.User;
import com.wlinsk.ansure.service.user.ScoringResultService;
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
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult>
    implements ScoringResultService {

    private final ScoringResultMapper scoringResultMapper;
    private final UserMapper userMapper;
    private final BusinessValidatorUtils businessValidatorUtils;
    private final BasicTransactionTemplate basicTransactionTemplate;
    private final AppMapper appMapper;
    @Override
    public void addScoringResult(AddScoringResultReqDTO reqDTO) {
        App oldApp = validateForAddAndUpdate(reqDTO.getAppId(), reqDTO.getResultProp(), reqDTO.getResultScoreRange());
        App updateApp = buildUpdateApp(oldApp);
        ScoringResult scoringResult = new ScoringResult();
        BeanUtils.copyProperties(reqDTO,scoringResult);
        scoringResult.init();
        scoringResult.setResultId(IdUtils.build(null));
        scoringResult.setUserId(BasicAuthContextUtils.getUserId());
        basicTransactionTemplate.execute(action -> {
            if (scoringResultMapper.insert(scoringResult) != 1){
                throw new BasicException(SysCode.DATABASE_INSERT_ERROR);
            }
            if (Objects.nonNull(updateApp)){
                if (appMapper.updateApp(updateApp) != 1){
                    throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
                }
            }
            return SysCode.success;
        });
    }

    @Override
    public void updateScoringResult(UpdateScoringResultReqDTO reqDTO) {
        ScoringResult scoringResult = scoringResultMapper.queryById(reqDTO.getResultId());
        Optional.ofNullable(scoringResult).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        if (!scoringResult.getAppId().equals(reqDTO.getAppId())){
            throw new BasicException(SysCode.DATA_NOT_FOUND);
        }
        App oldApp = validateForAddAndUpdate(reqDTO.getAppId(), reqDTO.getResultProp(), reqDTO.getResultScoreRange());
        App updateApp = buildUpdateApp(oldApp);
        ScoringResult update = new ScoringResult();
        BeanUtils.copyProperties(reqDTO,update);
        update.setVersion(scoringResult.getVersion());
        update.setUpdateTime(new Date());
        basicTransactionTemplate.execute(action -> {
            if (scoringResultMapper.updateScoringResult(update) != 1) {
                throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
            }
            if (Objects.nonNull(updateApp)){
                if (appMapper.updateApp(updateApp) != 1){
                    throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
                }
            }
            return SysCode.success;
        });
    }

    @Override
    public IPage<QueryScoringResultPageRespDTO> queryPage(QueryScoringResultPageReqDTO reqDTO) {
        Page<ScoringResult> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        IPage<ScoringResult> iPage = scoringResultMapper.queryPage(page,reqDTO.getAppId(),reqDTO.getResultName(),reqDTO.getResultDesc());
        if (CollectionUtils.isEmpty(iPage.getRecords())){
            return iPage.convert(scoringResult -> null);
        }
        List<String> userIdList = iPage.getRecords().stream().map(ScoringResult::getUserId).distinct().collect(Collectors.toList());
        Map<String, User> userMap = userMapper.queryByUserIdList(userIdList).stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getUserId, Function.identity()));
        return iPage.convert(scoringResult -> {
            QueryScoringResultPageRespDTO respDTO = new QueryScoringResultPageRespDTO();
            BeanUtils.copyProperties(scoringResult,respDTO);
            Optional.ofNullable(userMap.get(scoringResult.getUserId())).ifPresent(user -> {
                QueryUserDetailRespDTO userDTO = new QueryUserDetailRespDTO();
                BeanUtils.copyProperties(user, userDTO);
                respDTO.setUserInfo(userDTO);
            });
            return respDTO;
        });
    }

    @Override
    public void deleteById(String resultId) {
        ScoringResult scoringResult = scoringResultMapper.queryById(resultId);
        Optional.ofNullable(scoringResult).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        App oldApp = appMapper.queryByAppId(scoringResult.getAppId());
        Optional.ofNullable(oldApp).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        businessValidatorUtils.validateUserInfo(oldApp.getUserId());
        ScoringResult delete = new ScoringResult();
        delete.setResultId(scoringResult.getResultId());
        delete.setVersion(scoringResult.getVersion());
        delete.setUpdateTime(new Date());
        delete.setDelState(DelStateEnum.DEL);
        App updateApp = null;
        List<ScoringResult> scoringResults = scoringResultMapper.queryByAppIdOrderByScoreRange(scoringResult.getAppId());
        if (!CollectionUtils.isEmpty(scoringResults)){
            List<String> collect = scoringResults.stream().map(ScoringResult::getResultId).collect(Collectors.toList());
            if (collect.size() == 1 && collect.get(0).equals(scoringResult.getResultId())
                    && Arrays.asList(ReviewStatusEnum.REVIEW_PASS,ReviewStatusEnum.REVIEW_FAIL)
                    .contains(oldApp.getReviewStatus())){
                updateApp = new App();
                updateApp.setReviewStatus(ReviewStatusEnum.TO_BE_REVIEWED);
                updateApp.setAppId(oldApp.getAppId());
                updateApp.setUpdateTime(new Date());
                updateApp.setVersion(oldApp.getVersion());
            }
        }
        App finalUpdateApp = updateApp;
        basicTransactionTemplate.execute(action -> {
            if (scoringResultMapper.deleteScoringResult(delete) != 1){
                throw new BasicException(SysCode.DATABASE_DELETE_ERROR);
            }
            if (Objects.nonNull(finalUpdateApp)){
                if (appMapper.updateApp(finalUpdateApp) != 1){
                    throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
                }
            }
            return SysCode.success;
        });
    }

    private App validateForAddAndUpdate(String appId, List<String> resultProp,Integer resultScoreRange) {
        App app = appMapper.queryByAppId(appId);
        Optional.ofNullable(app).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        businessValidatorUtils.validateUserInfo(app.getUserId());
        if (AppTypeEnum.SCORE.equals(app.getAppType()) && Objects.isNull(resultScoreRange)){
            throw new BasicException(SysCode.SCORING_RESULT_RANGE_IS_NULL);
        }
        if (AppTypeEnum.TEST.equals(app.getAppType()) && CollectionUtils.isEmpty(resultProp)){
            throw new BasicException(SysCode.SCORING_PROP_IS_NULL);
        }
        return app;
    }
    private App buildUpdateApp(App oldApp){
        if (ReviewStatusEnum.REVIEW_PASS.equals(oldApp.getReviewStatus()) || ReviewStatusEnum.REVIEW_FAIL.equals(oldApp.getReviewStatus())){
            App update = new App();
            update.setReviewStatus(ReviewStatusEnum.TO_BE_REVIEWED);
            update.setAppId(oldApp.getAppId());
            update.setUpdateTime(new Date());
            update.setVersion(oldApp.getVersion());
            return update;
        }
        return null;
    }
}




