package com.example.synthetichumancore.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp = Instant.now();

    private int status;
    private String error;
    private String message;
    private String path;
}
