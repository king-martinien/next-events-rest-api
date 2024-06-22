package com.kingmartinien.nextevents.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDto {

    private Integer code;

    private String error;

    private LocalDateTime timestamp;

    private Set<String> validationErrors;

}
