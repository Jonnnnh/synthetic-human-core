package com.example.synthetichumancore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.audit")
@Data
public class AuditConfigProperties {
    private String mode;
    private String topic;
}
