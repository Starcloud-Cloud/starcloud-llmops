package com.starcloud.ops.business.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration()
public class ChatConfiguration {


    @Bean("CHAT_POOL_TASK_EXECUTOR")
    public ThreadPoolTaskExecutor notifyThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8); // 设置核心线程数
        executor.setMaxPoolSize(16); // 设置最大线程数
        executor.setKeepAliveSeconds(60); // 设置空闲时间
        executor.setQueueCapacity(100); // 设置队列大小
        executor.setThreadNamePrefix("chat-task-"); // 配置线程池的前缀
        // 进行加载
        executor.initialize();
        return executor;
    }
}
