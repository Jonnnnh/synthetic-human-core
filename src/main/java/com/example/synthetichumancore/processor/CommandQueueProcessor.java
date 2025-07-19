package com.example.synthetichumancore.processor;

import com.example.synthetichumancore.executor.CommandExecutor;
import com.example.synthetichumancore.model.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandQueueProcessor {

    private final BlockingQueue<Command> queue;
    private final CommandExecutor executor;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        Thread t = new Thread(this::processLoop, "shc-queue-processor");
        t.setDaemon(true);
        t.start();
    }

    private void processLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Command cmd = queue.take();
                log.info("shc-queue-processor: забрали из очереди {}", cmd);
                executor.execute(cmd);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
