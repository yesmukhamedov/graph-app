package com.example.graph.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneValueDto {
    private String value;
    private PhonePatternDto pattern;
}
