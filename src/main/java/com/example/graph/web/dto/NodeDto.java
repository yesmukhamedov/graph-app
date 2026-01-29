package com.example.graph.web.dto;

public class NodeDto {
    private final Long id;
    private final String name;

    public NodeDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}

