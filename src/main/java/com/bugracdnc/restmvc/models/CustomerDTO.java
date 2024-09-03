package com.bugracdnc.restmvc.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class CustomerDTO {
    @NotNull
    @NotBlank
    private String customerName;
    private UUID id;
    private Integer version;
    private String email;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}