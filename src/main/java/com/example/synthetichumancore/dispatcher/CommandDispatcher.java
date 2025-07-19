package com.example.synthetichumancore.dispatcher;

import com.example.synthetichumancore.enums.Priority;
import com.example.synthetichumancore.exception.QueueFullException;
import com.example.synthetichumancore.exception.ValidationException;
import com.example.synthetichumancore.executor.CommandExecutor;
import com.example.synthetichumancore.model.Command;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    private final CommandExecutor executorService;
    private final Validator validator;
    private final BlockingQueue<Command> queue;

    public void dispatch(Command cmd) {
        Set<ConstraintViolation<Command>> violations = validator.validate(cmd);
        if (!violations.isEmpty()) {
            ConstraintViolation<Command> v = violations.iterator().next();
            throw new ValidationException(v.getPropertyPath() + ": " + v.getMessage());
        }

        if (Priority.CRITICAL.equals(cmd.getPriority())) {
            log.info("CRITICAL: сразу исполняем {}", cmd);
            executorService.execute(cmd);
        } else {
            log.info("COMMON: кладём в очередь {}", cmd);
            boolean offered = queue.offer(cmd);
            if (!offered) {
                throw new QueueFullException("Command queue is full, please retry later");
            }
        }
    }
}
