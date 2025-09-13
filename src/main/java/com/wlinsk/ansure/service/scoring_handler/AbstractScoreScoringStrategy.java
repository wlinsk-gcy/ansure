package com.wlinsk.ansure.service.scoring_handler;

import com.wlinsk.ansure.mapper.QuestionMapper;
import com.wlinsk.ansure.mapper.ScoringResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: wlinsk
 * @Date: 2024/5/30
 */
@Slf4j
public abstract class AbstractScoreScoringStrategy implements ScoringStrategy{
    @Autowired
    protected QuestionMapper questionMapper;
    @Autowired
    protected ScoringResultMapper scoringResultMapper;
}
