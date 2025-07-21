package com.example.synthetichumancore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.audit")
public record AuditConfigProperties(
        @DefaultValue("console") String mode,
        @DefaultValue("audit.logs") String topic
) {
}
