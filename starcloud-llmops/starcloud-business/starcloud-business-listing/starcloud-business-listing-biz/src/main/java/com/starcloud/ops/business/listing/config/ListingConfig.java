package com.starcloud.ops.business.listing.config;

import cn.hutool.core.thread.BlockPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ListingConfig {

    @Bean("listingExecutor")
    public ThreadPoolTaskExecutor listingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int core = 4;
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(core * 2 + 1);
        executor.setKeepAliveSeconds(3);
        executor.setQueueCapacity(40);
        executor.setThreadNamePrefix("listing-execute-");
        executor.setRejectedExecutionHandler(new BlockPolicy());
        return executor;
    }
}
