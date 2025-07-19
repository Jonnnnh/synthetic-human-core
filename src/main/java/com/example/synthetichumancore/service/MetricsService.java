package com.example.synthetichumancore.service;

import com.example.synthetichumancore.model.Command;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final MeterRegistry registry;
    private final ThreadPoolTaskExecutor threadPoolExecutor;
    private final BlockingQueue<Command> commandQueue;

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Gauge.builder("commands.queue.size", commandQueue, BlockingQueue::size)
                .description("Number of pending common commands in the dispatch queue")
                .register(registry);

        Gauge.builder("shc.executor.queue.size", threadPoolExecutor,
                        ex -> ex.getThreadPoolExecutor().getQueue().size())
                .description("Number of tasks waiting in the executor pool")
                .register(registry);
    }

    public void incrementCounter(String author) {
        counters.computeIfAbsent(author, a -> Counter.builder("commands.executed")
                        .tag("author", a)
                        .register(registry))
                .increment();
    }
}
