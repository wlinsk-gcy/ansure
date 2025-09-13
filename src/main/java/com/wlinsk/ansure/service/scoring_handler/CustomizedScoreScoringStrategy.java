package com.wlinsk.ansure.service.scoring_handler;

import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import com.wlinsk.ansure.model.dto.question.QuestionOptionDTO;
import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.Question;
import com.wlinsk.ansure.model.entity.ScoringResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 自定义评分策略的得分类题目的处理器
 * @Author: wlinsk
 * @Date: 2024/5/30
 */
@Slf4j
@Component("CUSTOMIZED_SCORE_SCORING_STRATEGY")
public class CustomizedScoreScoringStrategy extends AbstractScoreScoringStrategy{
    @Override
    public ScoringResult doScore(List<String> choices, App app) {
        Question question = questionMapper.queryByAppId(app.getAppId());
        Optional.ofNullable(question).orElseThrow(() -> new BasicException(SysCode.DATA_NOT_FOUND));
        List<ScoringResult> scoringResultList = scoringResultMapper.queryByAppIdOrderByScoreRange(app.getAppId());
        if (CollectionUtils.isEmpty(scoringResultList)){
            throw new BasicException(SysCode.DATA_NOT_FOUND);
        }
        // 2. 统计用户的总得分
        int totalScore = 0;
        List<QuestionContentDTO> questionContent = question.getQuestionContent();
        // 遍历题目列表
        for (int i = 0; i < questionContent.size(); i++) {
            QuestionContentDTO questionContentDTO = questionContent.get(i);
            //获取所有大于0的选项
            Map<String, QuestionOptionDTO> optionMap = questionContentDTO.getOptions().stream().filter(option -> option.getScore() > 0)
                    .collect(Collectors.toMap(QuestionOptionDTO::getKey, Function.identity()));
            if (CollectionUtils.isEmpty(optionMap)){
                continue;
            }
            String answer = choices.get(i);
            QuestionOptionDTO option = optionMap.get(answer);
            if (StringUtils.isBlank(answer) || Objects.isNull(option)){
                continue;
            }
            totalScore += option.getScore();
        }
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                maxScoringResult = scoringResult;
                //因为是倒序集合，满足>=的条件就break；因为继续遍历的数据都是小于当前分数的
                break;
            }
        }
        maxScoringResult.setTotalScore(totalScore);
        return maxScoringResult;
    }
}
