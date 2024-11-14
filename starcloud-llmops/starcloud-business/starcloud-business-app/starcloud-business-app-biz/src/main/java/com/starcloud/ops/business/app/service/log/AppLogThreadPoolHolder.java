package com.starcloud.ops.business.app.service.log;

import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 小红书图片风格化线程池
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-27
 */
@Component
@Slf4j
public class AppLogThreadPoolHolder {

    /**
     * 阻塞队列
     */
    private static final LinkedBlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>(32);

    /**
     * 小红书图片风格化线程池
     */
    private static final ThreadPoolExecutor APP_LOG_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(8, 16,
            60, TimeUnit.SECONDS, BLOCKING_QUEUE, new PosterThreadFactory(), new AppLogRejectedExecutionHandler());

    /**
     * 获取线程池
     *
     * @return 线程池
     */
    public ThreadPoolExecutor executor() {
        return APP_LOG_THREAD_POOL_EXECUTOR;
    }

    public static <T> CompletableFuture<T> supplyAsync(Long tenantId, Long userId, Callable<T> callable) {
        return CompletableFuture.supplyAsync(
                () -> execute(tenantId, userId, callable),
                APP_LOG_THREAD_POOL_EXECUTOR
        );
    }

    /**
     * @param tenantId 租户编号
     * @param callable 逻辑
     */
    private static <V> V execute(Long tenantId, Long userId, Callable<V> callable) {
        Long oldTenantId = TenantContextHolder.getTenantId();
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            TenantContextHolder.setTenantId(tenantId);
            TenantContextHolder.setIgnore(false);
            UserContextHolder.setUserId(userId);
            // 执行逻辑
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            TenantContextHolder.setTenantId(oldTenantId);
            TenantContextHolder.setIgnore(oldIgnore);
            UserContextHolder.clear();
        }
    }

    /**
     * 线程池工厂
     *
     * @author nacoyer
     * @version 1.0.0
     * @since 2023-11-27
     */
    private static class PosterThreadFactory implements ThreadFactory {

        /**
         * 新建线程
         *
         * @param runnable 线程执行的任务
         * @return 线程
         */
        @Override
        public Thread newThread(@NotNull Runnable runnable) {
            Thread thread = new Thread(runnable, "app-log-thread-");
            thread.setDaemon(true);
            return thread;
        }
    }

    /**
     * 拒绝策略
     */
    private static class AppLogRejectedExecutionHandler implements RejectedExecutionHandler {

        /**
         * 拒绝执行，不做任何处理
         *
         * @param runnable 线程执行的任务
         * @param executor 线程池
         */
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            log.warn("The app-log-thread pool is full");
        }

    }
}
