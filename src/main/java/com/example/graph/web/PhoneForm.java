package com.example.graph.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PhoneForm {
    @NotNull(message = "Node is required.")
    private Long nodeId;

    @NotNull(message = "Pattern is required.")
    private Long patternId;

    @NotBlank(message = "Value is required.")
    @Size(max = 32, message = "Value must be at most 32 characters.")
    private String value;
}
