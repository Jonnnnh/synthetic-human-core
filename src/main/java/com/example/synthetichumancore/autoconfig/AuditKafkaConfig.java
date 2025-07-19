package com.example.synthetichumancore.autoconfig;

import com.example.synthetichumancore.aspect.AuditEvent;
import com.example.synthetichumancore.config.AuditConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@ConditionalOnProperty(name = "app.audit.mode", havingValue = "kafka")
@EnableConfigurationProperties(AuditConfigProperties.class)
public class AuditKafkaConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, AuditEvent> auditKafkaTemplate(
            ProducerFactory<String, AuditEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
