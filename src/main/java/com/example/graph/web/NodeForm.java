package com.example.graph.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NodeForm {
    @NotBlank(message = "Name is required.")
    @Size(max = 200, message = "Name must be at most 200 characters.")
    private String name;
}
