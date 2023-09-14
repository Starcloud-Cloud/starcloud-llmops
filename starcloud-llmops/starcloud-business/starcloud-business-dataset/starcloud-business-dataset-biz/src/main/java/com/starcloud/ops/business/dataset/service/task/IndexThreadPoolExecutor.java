package com.starcloud.ops.business.dataset.service.task;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

@Slf4j
public class IndexThreadPoolExecutor {

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 32,
            60, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(500), new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, "IndexThreadPoolExecutor");
        }
    }, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("IndexThreadPoolExecutor Thread pool is full");
            throw new RuntimeException("IndexThreadPoolExecutor Thread pool is full");
        }
    });

    public static void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }
}
