package com.example.synthetichumancore.config;

import com.example.synthetichumancore.model.Command;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class DispatcherConfig {

    @Bean
    @ConditionalOnMissingBean
    public BlockingQueue<Command> commandQueue(ThreadPoolConfigProperties props) {
        return new LinkedBlockingQueue<>(props.queueCapacity());
    }
}
