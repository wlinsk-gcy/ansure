package com.wlinsk.ansure.service.scoring_handler;

import com.wlinsk.ansure.basic.enums.AppTypeEnum;
import com.wlinsk.ansure.basic.enums.ScoringStrategyEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 得分策略器工厂
 * @Author: wlinsk
 * @Date: 2024/5/30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScoringStrategyFactory {
    private final static String BEAN_NAME = "%s_%s_SCORING_STRATEGY";
    private final Map<String, ScoringStrategy> map;
    public ScoringStrategy getHandler(ScoringStrategyEnum scoringStrategy, AppTypeEnum appType){
        ScoringStrategy strategy = map.get(String.format(BEAN_NAME, scoringStrategy.name(), appType.name()));
        Optional.ofNullable(strategy).orElseThrow(() -> new BasicException(SysCode.SYSTEM_STRATEGY_HANDLER_NO_FOUND));
        return strategy;
    }
}
