package com.example.synthetichumancore.aspect;

import com.example.synthetichumancore.config.AuditConfigProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditConfigProperties props;
    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;
    private final ObjectMapper mapper;

    @Pointcut("@annotation(watch)")
    public void watchPointcut(WeylandWatchingYou watch) { }

    @Before(value = "watchPointcut(watch)", argNames = "jp,watch")
    public void beforeAudit(JoinPoint jp, WeylandWatchingYou watch) {
        AuditEvent ev = buildBaseEvent(jp, watch);
        sendEvent("enter", ev);
    }

    @AfterReturning(pointcut = "watchPointcut(watch)", returning = "result", argNames = "jp,watch,result")
    public void afterReturning(JoinPoint jp, WeylandWatchingYou watch, Object result) {
        AuditEvent ev = buildBaseEvent(jp, watch);
        ev.setResult(serializeOrPlaceholder(result));
        sendEvent("exit", ev);
    }

    @AfterThrowing(pointcut = "watchPointcut(watch)", throwing = "ex", argNames = "jp,watch,ex")
    public void afterThrowing(JoinPoint jp, WeylandWatchingYou watch, Throwable ex) {
        AuditEvent ev = buildBaseEvent(jp, watch);
        ev.setException(stacktraceToString(ex));
        sendEvent("error", ev);
    }

    private AuditEvent buildBaseEvent(JoinPoint jp, WeylandWatchingYou watch) {
        AuditEvent ev = new AuditEvent();
        ev.setTimestamp(Instant.now());
        ev.setMethod(jp.getSignature().toShortString());
        ev.setLevel(watch.level());
        ev.setArgs(serializeOrPlaceholder(jp.getArgs()));
        return ev;
    }

    private void sendEvent(String phase, AuditEvent ev) {
        ev.setPhase(phase);
        if ("kafka".equalsIgnoreCase(props.mode())) {
            CompletableFuture<?> future = kafkaTemplate.send(props.topic(), ev);
            future.whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Не удалось отправить AuditEvent ({}): {}", phase, ev, ex);
                } else {
                    log.debug("AuditEvent успешно отправлен ({}): {}", phase, ev);
                }
            });
        } else {
            log.info("[AUDIT][{}] {}", phase.toUpperCase(), ev);
        }
    }

    private String serializeOrPlaceholder(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "<unserializable>";
        }
    }

    private String stacktraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}