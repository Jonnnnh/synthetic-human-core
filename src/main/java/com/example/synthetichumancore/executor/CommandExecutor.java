package com.example.synthetichumancore.executor;

import com.example.synthetichumancore.model.Command;
import com.example.synthetichumancore.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommandExecutor {
    private final MetricsService metricsService;

    @Async("syntheticHumanCoreExecutor")
    public void execute(Command cmd) {
        try {
            log.info("Executing command: {}", cmd);
            Thread.sleep(100);
            metricsService.incrementCounter(cmd.getAuthor());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Execution interrupted for command {}", cmd, e);
        }
    }
}
