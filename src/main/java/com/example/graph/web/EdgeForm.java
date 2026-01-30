package com.example.graph.web;

import lombok.Data;

@Data
public class EdgeForm {
    private String fromId;
    private String toId;
    private String labelValue;
    private String newLabel;
    private String createdAt;
    private String expiredAt;
}
