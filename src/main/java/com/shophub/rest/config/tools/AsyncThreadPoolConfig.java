package com.shophub.rest.config.tools;

import com.shophub.rest.config.CommonEnvConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncThreadPoolConfig {
    private final CommonEnvConfig envConfig;

    @Bean(name = "emailPoolExecutor")
    public Executor emailPoolExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(envConfig.ASYNC_THREAD_POOL_MIN_SIZE());
        executor.setMaxPoolSize(envConfig.ASYNC_THREAD_POOL_MAX_SIZE());

        executor.setQueueCapacity(envConfig.ASYNC_THREAD_POOL_QUEUE_CAPACITY());
        executor.setThreadNamePrefix(envConfig.ASYNC_THREAD_POOL_PREFIX());

        executor.initialize();
        return executor;
    }
}
