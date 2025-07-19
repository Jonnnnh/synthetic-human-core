package com.example.synthetichumancore.dto;

import com.example.synthetichumancore.enums.Priority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandDto {

    @NotNull
    @Size(max = 1000)
    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    @Size(max = 100)
    private String author;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String time;
}
