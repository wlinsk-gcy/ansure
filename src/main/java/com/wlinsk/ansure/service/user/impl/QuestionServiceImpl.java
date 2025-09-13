package com.wlinsk.ansure.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlinsk.ansure.basic.enums.AppTypeEnum;
import com.wlinsk.ansure.basic.enums.ReviewStatusEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.id_generator.IdUtils;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.basic.utils.BusinessValidatorUtils;
import com.wlinsk.ansure.mapper.AppMapper;
import com.wlinsk.ansure.mapper.QuestionMapper;
import com.wlinsk.ansure.mapper.UserMapper;
import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import com.wlinsk.ansure.model.dto.question.QuestionOptionDTO;
import com.wlinsk.ansure.model.dto.question.req.AddQuestionReqDTO;
import com.wlinsk.ansure.model.dto.question.req.QueryQuestionPageReqDTO;
import com.wlinsk.ansure.model.dto.question.req.UpdateQuestionReqDTO;
import com.wlinsk.ansure.model.dto.question.resp.QueryQuestionPageRespDTO;
import com.wlinsk.ansure.model.dto.user.resp.QueryUserDetailRespDTO;
import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.Question;
import com.wlinsk.ansure.model.entity.User;
import com.wlinsk.ansure.service.user.QuestionService;
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
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final BusinessValidatorUtils businessValidatorUtils;
    private final BasicTransactionTemplate basicTransactionTemplate;
    private final AppMapper appMapper;

    @Override
    public void addQuestion(AddQuestionReqDTO reqDTO) {
        App app = validateForAddAndUpdate(reqDTO.getAppId(), reqDTO.getQuestionContent());
        Question question = new Question();
        question.init();
        question.setQuestionId(IdUtils.build(null));
        question.setUserId(BasicAuthContextUtils.getUserId());
        question.setQuestionContent(reqDTO.getQuestionContent());
        question.setAppId(reqDTO.getAppId());
        App update = buildUpdateApp(app);
        basicTransactionTemplate.execute(action -> {
            if (questionMapper.insert(question) != 1) {
                throw new BasicException(SysCode.DATABASE_INSERT_ERROR);
            }
            if (Objects.nonNull(update)){
                if (appMapper.updateApp(update) != 1){
                    throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
                }
            }
            return SysCode.success;
        });
    }

    @Override
    public IPage<QueryQuestionPageRespDTO> queryPage(QueryQuestionPageReqDTO reqDTO) {
        Page<Question> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        IPage<Question> result = questionMapper.queryPage(page, reqDTO.getAppId());
        if (CollectionUtils.isEmpty(result.getRecords())) {
            return result.convert(question -> null);
        }
        List<String> userIdList = result.getRecords().stream().map(Question::getUserId).distinct().collect(Collectors.toList());
        Map<String, User> userMap = userMapper.queryByUserIdList(userIdList).stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getUserId, Function.identity()));
        return result.convert(question -> {
            QueryQuestionPageRespDTO respDTO = new QueryQuestionPageRespDTO();
            BeanUtils.copyProperties(question, respDTO);
            Optional.ofNullable(userMap.get(question.getUserId())).ifPresent(user -> {
                QueryUserDetailRespDTO userDTO = new QueryUserDetailRespDTO();
                BeanUtils.copyProperties(user, userDTO);
                respDTO.setUserInfo(userDTO);
            });
            return respDTO;
        });
    }

    @Override
    public void updateQuestion(UpdateQuestionReqDTO reqDTO) {
        Question question = questionMapper.queryById(reqDTO.getQuestionId());
        Optional.ofNullable(question).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        App oldApp = validateForAddAndUpdate(question.getAppId(), reqDTO.getQuestionContent());
        App updateApp = buildUpdateApp(oldApp);
        Question update = new Question();
        update.setQuestionId(question.getQuestionId());
        update.setVersion(question.getVersion());
        update.setUpdateTime(new Date());
        update.setQuestionContent(reqDTO.getQuestionContent());
        basicTransactionTemplate.execute(action -> {
            if (questionMapper.updateQuestion(update) != 1) {
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

    private App validateForAddAndUpdate(String appId,List<QuestionContentDTO> questionContent){
        App app = appMapper.queryByAppId(appId);
        Optional.ofNullable(app).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        businessValidatorUtils.validateUserInfo(app.getUserId());
        if (AppTypeEnum.SCORE.equals(app.getAppType())) {
            questionContent.stream().map(QuestionContentDTO::getOptions)
                    .map(list -> list.stream().map(QuestionOptionDTO::getScore).reduce(0, Integer::sum))
                    .filter(score -> score <= 0).findAny().ifPresent(score -> {
                        throw new BasicException(SysCode.SCORE_NON_ZERO);
                    });
        }
        return app;
    }


}




