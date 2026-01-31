package com.example.graph.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserForm {
    @NotNull(message = "Node is required.")
    private Long nodeId;

    @NotBlank(message = "Phone digits are required.")
    @Size(max = 32, message = "Phone digits must be at most 32 characters.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone digits must contain only numbers.")
    private String phoneDigits;
}
