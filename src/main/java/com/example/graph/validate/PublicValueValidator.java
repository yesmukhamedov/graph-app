package com.example.graph.validate;

import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.web.form.EdgeValueForm;
import com.example.graph.web.form.NodeValueForm;
import org.springframework.stereotype.Component;

@Component
public class PublicValueValidator {
    private static final int MAX_VALUE_LENGTH = 200;

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;

    public PublicValueValidator(NodeRepository nodeRepository, EdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    public void validate(NodeValueForm form) {
        if (form.getNodeId() == null) {
            throw new ValidationException("Node is required.");
        }
        if (form.getValue() == null || form.getValue().isBlank()) {
            throw new ValidationException("Value is required.");
        }
        if (form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            throw new ValidationException("Value must be at most 200 characters.");
        }
        if (!nodeRepository.existsById(form.getNodeId())) {
            throw new ValidationException("Node not found.");
        }
    }

    public void validate(EdgeValueForm form) {
        if (form.getEdgeId() == null) {
            throw new ValidationException("Edge is required.");
        }
        if (form.getValue() == null || form.getValue().isBlank()) {
            throw new ValidationException("Value is required.");
        }
        if (form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            throw new ValidationException("Value must be at most 200 characters.");
        }
        if (!edgeRepository.existsById(form.getEdgeId())) {
            throw new ValidationException("Edge not found.");
        }
    }
}
