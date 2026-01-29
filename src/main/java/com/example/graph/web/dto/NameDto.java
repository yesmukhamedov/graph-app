package com.example.graph.web.dto;

public class NameDto {
    private final Long id;
    private final String text;

    public NameDto(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
