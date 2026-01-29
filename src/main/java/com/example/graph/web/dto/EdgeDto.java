package com.example.graph.web.dto;

public class EdgeDto {

    private final Long fromId;
    private final Long toId;

    public EdgeDto(Long fromId, Long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getToId() {
        return toId;
    }
}
