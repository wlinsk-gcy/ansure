package com.wlinsk.ansure.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlinsk.ansure.basic.enums.DelStateEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.id_generator.IdUtils;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.basic.utils.BusinessValidatorUtils;
import com.wlinsk.ansure.mapper.UserAnswerRecordMapper;
import com.wlinsk.ansure.mapper.UserMapper;
import com.wlinsk.ansure.model.dto.answer.req.AddUserAnswerReqDTO;
import com.wlinsk.ansure.model.dto.answer.req.QueryUserAnswerPageReqDTO;
import com.wlinsk.ansure.model.dto.answer.resp.QueryUserAnswerRecordDetailsRespDTO;
import com.wlinsk.ansure.model.dto.user.resp.QueryUserDetailRespDTO;
import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.ScoringResult;
import com.wlinsk.ansure.model.entity.User;
import com.wlinsk.ansure.model.entity.UserAnswerRecord;
import com.wlinsk.ansure.service.scoring_handler.ScoringStrategyFactory;
import com.wlinsk.ansure.service.user.UserAnswerRecordService;
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
public class UserAnswerRecordServiceImpl extends ServiceImpl<UserAnswerRecordMapper, UserAnswerRecord>
    implements UserAnswerRecordService {

    private final UserAnswerRecordMapper userAnswerRecordMapper;
    private final UserMapper userMapper;
    private final BusinessValidatorUtils businessValidatorUtils;
    private final BasicTransactionTemplate basicTransactionTemplate;
    private final ScoringStrategyFactory scoringStrategyFactory;

    @Override
    public String addUserAnswer(AddUserAnswerReqDTO reqDTO) {
        App app = businessValidatorUtils.validateAppInfo(reqDTO.getAppId());
        ScoringResult score = scoringStrategyFactory.getHandler(app.getScoringStrategy(), app.getAppType())
                .doScore(reqDTO.getChoices(), app);
        UserAnswerRecord record = new UserAnswerRecord();
        record.init();
        record.setRecordId(IdUtils.build(null));
        record.setUserId(BasicAuthContextUtils.getUserId());
        record.setAppId(app.getAppId());
        record.setAppType(app.getAppType());
        record.setScoringStrategy(app.getScoringStrategy());
        record.setChoices(reqDTO.getChoices());
        record.setResultId(score.getResultId());
        record.setResultName(score.getResultName());
        record.setResultDesc(score.getResultDesc());
        record.setResultPicture(score.getResultPicture());
        record.setResultScore(score.getTotalScore());
        basicTransactionTemplate.execute(action -> {
            if (userAnswerRecordMapper.insert(record) != 1){
                throw new BasicException(SysCode.DATABASE_INSERT_ERROR);
            }
            return SysCode.success;
        });
        return record.getRecordId();
    }

    @Override
    public QueryUserAnswerRecordDetailsRespDTO queryById(String recordId) {
        UserAnswerRecord record = userAnswerRecordMapper.queryById(recordId);
        Optional.ofNullable(record).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        if (!BasicAuthContextUtils.getUserId().equals(record.getUserId())){
            throw new BasicException(SysCode.DATA_NOT_FOUND);
        }
        User user = userMapper.queryByUserId(record.getUserId());
        QueryUserAnswerRecordDetailsRespDTO respDTO = new QueryUserAnswerRecordDetailsRespDTO();
        BeanUtils.copyProperties(record,respDTO);
        QueryUserDetailRespDTO userDTO = new QueryUserDetailRespDTO();
        BeanUtils.copyProperties(user,userDTO);
        respDTO.setUserInfo(userDTO);
        return respDTO;
    }

    @Override
    public IPage<QueryUserAnswerRecordDetailsRespDTO> queryPage(QueryUserAnswerPageReqDTO reqDTO) {
        Page<UserAnswerRecord> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        IPage<UserAnswerRecord> iPage = userAnswerRecordMapper.queryPage(page,BasicAuthContextUtils.getUserId()
                ,reqDTO.getAppId(),reqDTO.getRecordId(),reqDTO.getResultName());
        if (CollectionUtils.isEmpty(iPage.getRecords())){
            return iPage.convert(record -> null);
        }
        List<String> userIds = iPage.getRecords().stream().map(UserAnswerRecord::getUserId)
                .distinct().collect(Collectors.toList());
        Map<String, User> userMap = userMapper.queryByUserIdList(userIds).stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(User::getUserId, Function.identity()));
        return iPage.convert(record -> {
            QueryUserAnswerRecordDetailsRespDTO respDTO = new QueryUserAnswerRecordDetailsRespDTO();
            BeanUtils.copyProperties(record,respDTO);
            User user = userMap.get(record.getUserId());
            if (Objects.nonNull(user)){
                QueryUserDetailRespDTO userDTO = new QueryUserDetailRespDTO();
                BeanUtils.copyProperties(user,userDTO);
                respDTO.setUserInfo(userDTO);
            }
            return respDTO;
        });
    }

    @Override
    public void deleteById(String recordId) {
        UserAnswerRecord record = userAnswerRecordMapper.queryById(recordId);
        Optional.ofNullable(record).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        businessValidatorUtils.validateAppInfo(record.getAppId());
        if (!record.getUserId().equals(BasicAuthContextUtils.getUserId())){
            throw new BasicException(SysCode.SYSTEM_NO_PERMISSION);
        }
        UserAnswerRecord delete = new UserAnswerRecord();
        delete.setRecordId(record.getRecordId());
        delete.setUpdateTime(new Date());
        delete.setVersion(record.getVersion());
        delete.setDelState(DelStateEnum.DEL);
        basicTransactionTemplate.execute(action -> {
            if (userAnswerRecordMapper.deleteRecord(delete) != 1){
                throw new BasicException(SysCode.DATABASE_DELETE_ERROR);
            }
            return SysCode.success;
        });
    }
}




