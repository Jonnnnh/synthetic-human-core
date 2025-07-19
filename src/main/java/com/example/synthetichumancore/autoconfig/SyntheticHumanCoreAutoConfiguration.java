package com.example.synthetichumancore.autoconfig;

import com.example.synthetichumancore.config.ThreadPoolConfigProperties;
import com.example.synthetichumancore.config.AuditConfigProperties;
import com.example.synthetichumancore.dispatcher.CommandDispatcher;
import com.example.synthetichumancore.executor.CommandExecutor;
import com.example.synthetichumancore.model.Command;
import com.example.synthetichumancore.service.MetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Validator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@AutoConfiguration
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties({
        ThreadPoolConfigProperties.class,
        AuditConfigProperties.class
})
public class SyntheticHumanCoreAutoConfiguration {

    @Bean(name = {"threadPoolTaskExecutor", "syntheticHumanCoreExecutor"})
    @Primary
    @ConditionalOnMissingBean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(ThreadPoolConfigProperties props) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(props.corePoolSize());
        executor.setMaxPoolSize(props.maxPoolSize());
        executor.setQueueCapacity(props.queueCapacity());
        executor.setThreadNamePrefix("shc-exec-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public BlockingQueue<Command> commandQueue(
            ThreadPoolConfigProperties props) {
        return new LinkedBlockingQueue<>(props.queueCapacity());
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandDispatcher commandDispatcher(
            CommandExecutor executorService,
           Validator validator,
            BlockingQueue<Command> queue
    ) {
        return new CommandDispatcher(
                executorService, validator, queue
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandExecutor commandExecutor(MetricsService metricsService) {
        return new CommandExecutor(metricsService);
    }

    @Bean
    @ConditionalOnMissingBean
    public MetricsService metricsService(
            MeterRegistry registry,
            ThreadPoolTaskExecutor threadPoolTaskExecutor,
            BlockingQueue<Command> commandQueue
    ) {
        return new MetricsService(registry, threadPoolTaskExecutor, commandQueue);
    }
}