package com.example.synthetichumancore.aspect;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AuditEvent {
    private Instant timestamp;
    private String method;
    private String phase;
    private AuditLevel level;
    private String args;
    private String result;
    private String exception;
}
