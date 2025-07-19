package com.example.synthetichumancore.model;

import com.example.synthetichumancore.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Command {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String description;
    private Priority priority;
    private String author;
    private Instant time;
    private String status;
}
