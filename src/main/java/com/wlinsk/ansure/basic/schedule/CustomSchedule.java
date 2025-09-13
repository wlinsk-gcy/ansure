package com.wlinsk.ansure.basic.schedule;

import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.transaction.BasicTransactionTemplate;
import com.wlinsk.ansure.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: wlinsk
 * @Date: 2025/7/10
 */
@Slf4j
@Component
public class CustomSchedule {

    @Autowired
    private BasicTransactionTemplate basicTransactionTemplate;
    @Autowired
    private UserMapper userMapper;
    @Scheduled(cron = "0 0 0 * * ?") // 每日 0 点执行一次
    public void resetUserAiPoint() {
        log.info("定时任务--重置用户AI积分任务执行");
        try {
            basicTransactionTemplate.execute(action -> {
                int point = userMapper.resetUserAiPoint();
                if (point <= 0) {
                    log.error("定时任务--重置用户AI积分任务执行失败");
                    throw new BasicException(SysCode.DATABASE_UPDATE_ERROR);
                }
                return SysCode.success;
            });
        } catch (Exception e) {
            log.error("定时任务--重置用户AI积分任务执行异常：", e);
        }
    }
}
