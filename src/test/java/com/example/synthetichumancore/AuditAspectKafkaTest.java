package com.example.synthetichumancore;

import com.example.synthetichumancore.aspect.AuditEvent;
import com.example.synthetichumancore.controller.CommandController;
import com.example.synthetichumancore.dto.CommandDto;
import com.example.synthetichumancore.enums.Priority;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                SyntheticHumanCoreApplication.class,
                AuditAspectKafkaTest.TestConsumerConfig.class
        },
        properties = {
                "app.audit.mode=kafka",
                "app.audit.topic=audit-test"
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@EmbeddedKafka(partitions = 1, topics = {"audit-test"})
class AuditAspectKafkaTest {

    @Autowired
    private CommandController controller;

    @Autowired
    private EmbeddedKafkaBroker broker;

    private static final BlockingQueue<AuditEvent> records = new LinkedBlockingQueue<>();

    @BeforeEach
    void clear() {
        records.clear();
    }

    @Test
    void whenControllerSendCalled_thenAuditEventSent() throws InterruptedException {
        var dto = CommandDto.builder()
                .description("hello")
                .priority(Priority.COMMON)
                .author("tester")
                .time(Instant.now().toString())
                .build();

        controller.send(dto);

        AuditEvent ev = records.poll(5, TimeUnit.SECONDS);
        assertThat(ev).isNotNull();
        assertThat(ev.getPhase()).isEqualTo("enter");
        assertThat(ev.getMethod()).contains("CommandController.send");
        assertThat(ev.getArgs()).contains("hello");
    }

    @Configuration
    static class TestConsumerConfig {
        @Bean
        public ConsumerFactory<String, AuditEvent> consumerFactory(EmbeddedKafkaBroker b) {
            var props = KafkaTestUtils.consumerProps("g1", "true", b);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            var deser = new JsonDeserializer<>(AuditEvent.class);
            deser.addTrustedPackages("*");
            return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deser);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, AuditEvent> kafkaListenerContainerFactory(
                ConsumerFactory<String, AuditEvent> cf
        ) {
            var f = new ConcurrentKafkaListenerContainerFactory<String, AuditEvent>();
            f.setConsumerFactory(cf);
            return f;
        }
    }

    @KafkaListener(
            topics = "audit-test",
            groupId = "g1",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, AuditEvent> rec) {
        records.add(rec.value());
    }

}