package com.example.synthetichumancore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.executor")
public record ThreadPoolConfigProperties(
        @DefaultValue("4") int corePoolSize,
        @DefaultValue("10") int maxPoolSize,
        @DefaultValue("100") int queueCapacity
) {
}
