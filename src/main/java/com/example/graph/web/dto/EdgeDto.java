package com.example.graph.web.dto;

import java.time.OffsetDateTime;

public class EdgeDto {
    private final Long id;
    private final Long fromId;
    private final Long toId;
    private final String label;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime expiredAt;
    private final String fromName;
    private final String toName;

    public EdgeDto(Long id, Long fromId, Long toId, String label,
                   OffsetDateTime createdAt, OffsetDateTime expiredAt,
                   String fromName, String toName) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.label = label;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.fromName = fromName;
        this.toName = toName;
    }

    public Long getId() {
        return id;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getToId() {
        return toId;
    }

    public String getLabel() {
        return label;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getExpiredAt() {
        return expiredAt;
    }

    public String getFromName() {
        return fromName;
    }

    public String getToName() {
        return toName;
    }
}
