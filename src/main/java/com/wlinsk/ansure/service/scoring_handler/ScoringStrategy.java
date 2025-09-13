package com.wlinsk.ansure.service.scoring_handler;


import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.ScoringResult;

import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2024/5/30
 */
public interface ScoringStrategy {
    ScoringResult doScore(List<String> choices, App app);
}
