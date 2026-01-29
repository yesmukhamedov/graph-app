package com.example.graph.service;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NameEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.NameRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.web.dto.EdgeDto;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;
    private final NameRepository nameRepository;

    public EdgeService(EdgeRepository edgeRepository, NodeRepository nodeRepository, NameRepository nameRepository) {
        this.edgeRepository = edgeRepository;
        this.nodeRepository = nodeRepository;
        this.nameRepository = nameRepository;
    }

    public EdgeEntity createEdge(Long fromId, Long toId, String labelText,
                                 LocalDateTime createdAt, LocalDateTime expiredAt) {
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("Both from and to nodes are required.");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Self-loops are not allowed.");
        }
        NodeEntity fromNode = nodeRepository.findById(fromId)
            .orElseThrow(() -> new IllegalArgumentException("From node not found."));
        NodeEntity toNode = nodeRepository.findById(toId)
            .orElseThrow(() -> new IllegalArgumentException("To node not found."));
        if (edgeRepository.existsByFromNodeIdAndToNodeId(fromId, toId)) {
            throw new IllegalArgumentException("That edge already exists.");
        }
        OffsetDateTime createdAtOffset = toOffsetDateTime(createdAt);
        OffsetDateTime expiredAtOffset = toOffsetDateTime(expiredAt);
        if (createdAtOffset != null && expiredAtOffset != null && createdAtOffset.isAfter(expiredAtOffset)) {
            throw new IllegalArgumentException("Created time must be before expired time.");
        }

        EdgeEntity edge = new EdgeEntity();
        edge.setFromNode(fromNode);
        edge.setToNode(toNode);
        edge.setCreatedAt(createdAtOffset);
        edge.setExpiredAt(expiredAtOffset);

        if (labelText != null && !labelText.trim().isEmpty()) {
            NameEntity label = new NameEntity();
            label.setText(labelText.trim());
            label.setCreatedAt(OffsetDateTime.now());
            edge.setLabel(nameRepository.save(label));
        }
        return edgeRepository.save(edge);
    }

    @Transactional(readOnly = true)
    public List<EdgeDto> listEdges() {
        return edgeRepository.findAll().stream()
            .map(edge -> new EdgeDto(
                edge.getId(),
                edge.getFromNode().getId(),
                edge.getToNode().getId(),
                edge.getLabel() == null ? null : edge.getLabel().getText(),
                edge.getCreatedAt(),
                edge.getExpiredAt(),
                edge.getFromNode().getName().getText(),
                edge.getToNode().getName().getText()
            ))
            .toList();
    }

    public void deleteEdge(Long id) {
        edgeRepository.deleteById(id);
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
