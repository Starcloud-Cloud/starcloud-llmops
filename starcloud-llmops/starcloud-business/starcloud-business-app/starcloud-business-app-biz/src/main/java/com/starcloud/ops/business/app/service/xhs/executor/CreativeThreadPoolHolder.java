package com.starcloud.ops.business.app.service.xhs.executor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

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
public class CreativeThreadPoolHolder {

    /**
     * 阻塞队列
     */
    private static final LinkedBlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>(32);

    /**
     * 小红书图片风格化线程池
     */
    private static final ThreadPoolExecutor XHS_IMAGE_CREATIVE_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(8, 16,
            60, TimeUnit.SECONDS, BLOCKING_QUEUE, new CreativeThreadFactory(), new XhsImageCreativeRejectedExecutionHandler());

    /**
     * 获取线程池
     *
     * @return 线程池
     */
    public ThreadPoolExecutor executor() {
        return XHS_IMAGE_CREATIVE_THREAD_POOL_EXECUTOR;
    }

    /**
     * 线程池工厂
     *
     * @author nacoyer
     * @version 1.0.0
     * @since 2023-11-27
     */
    private static class CreativeThreadFactory implements ThreadFactory {

        /**
         * 新建线程
         *
         * @param runnable 线程执行的任务
         * @return 线程
         */
        @Override
        public Thread newThread(@NotNull Runnable runnable) {
            Thread thread = new Thread(runnable, "creative-content-thread-");
            thread.setDaemon(true);
            return thread;
        }
    }

    /**
     * 拒绝策略
     */
    private static class XhsImageCreativeRejectedExecutionHandler implements RejectedExecutionHandler {

        /**
         * 拒绝执行，不做任何处理
         *
         * @param runnable 线程执行的任务
         * @param executor 线程池
         */
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            log.warn("The xhs-image-creative-thread pool is full");
        }

    }
}
