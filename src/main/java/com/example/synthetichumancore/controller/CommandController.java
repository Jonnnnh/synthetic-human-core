package com.example.synthetichumancore.controller;

import com.example.synthetichumancore.aspect.AuditLevel;
import com.example.synthetichumancore.dto.CommandDto;
import com.example.synthetichumancore.model.Command;
import com.example.synthetichumancore.dispatcher.CommandDispatcher;
import com.example.synthetichumancore.aspect.WeylandWatchingYou;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commands")
public class CommandController {

    private final CommandDispatcher dispatcher;


    @PostMapping
    @WeylandWatchingYou(level = AuditLevel.INFO)
    public ResponseEntity<Void> send(@Valid @RequestBody CommandDto dto) {
        Command cmd = Command.builder()
                .id(UUID.randomUUID())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .author(dto.getAuthor())
                .time(Instant.parse(dto.getTime()))
                .status("PENDING")
                .build();

        dispatcher.dispatch(cmd);
        return ResponseEntity.accepted().build();
    }
}
