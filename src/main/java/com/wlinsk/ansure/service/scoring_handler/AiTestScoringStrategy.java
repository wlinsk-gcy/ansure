package com.wlinsk.ansure.service.scoring_handler;

import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.ScoringResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 由AI自动评分的评分策略的测试类题目处理器
 * @Author: wlinsk
 * @Date: 2024/5/30
 */
@Slf4j
@Component("AI_SCORE_TEST_SCORING_STRATEGY")
public class AiTestScoringStrategy extends AbstractScoreScoringStrategy{
    @Override
    public ScoringResult doScore(List<String> choices, App app) {
        log.info("AI自动评分的评分策略的测试类题目处理器");
        return null;
    }
}
