package com.example.synthetichumancore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.executor")
public record ThreadPoolConfigProperties(
    int corePoolSize,
    int maxPoolSize,
    int queueCapacity
) {}
