package com.wlinsk.ansure.basic.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Slf4j
@Component
public class ThreadPoolUtils {
    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(
            100);

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 10, 180, TimeUnit.SECONDS,
            queue, new CustomizableThreadFactory("custom-thread-"), new ThreadPoolExecutor.CallerRunsPolicy(){}
    );
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    @PostConstruct
    private void runThread() {
        try {
            executor.prestartCoreThread();
            log.info("线程池初始化……");
        } catch (Exception e) {
            log.error("线程池初始化异常", e);
        }
    }
}
