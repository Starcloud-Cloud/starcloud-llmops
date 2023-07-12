package com.starcloud.ops.business.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Configuration()
public class ThreadPoolConfiguration {


    @Bean("CHAT_POOL_EXECUTOR")
    public ThreadPoolExecutor chatThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8,36,
                60, TimeUnit.MICROSECONDS,new SynchronousQueue<>());
        return executor;
    }

    @Bean("APP_POOL_EXECUTOR")
    public ThreadPoolExecutor appThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8,36,
                60, TimeUnit.MICROSECONDS,new SynchronousQueue<>());
        return executor;
    }
}
