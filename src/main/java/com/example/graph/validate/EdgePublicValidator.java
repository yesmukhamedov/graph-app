package com.example.graph.validate;

import com.example.graph.repository.NodeRepository;
import com.example.graph.web.form.EdgePublicForm;
import org.springframework.stereotype.Component;

@Component
public class EdgePublicValidator {
    private final NodeRepository nodeRepository;

    public EdgePublicValidator(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public void validate(EdgePublicForm form) {
        if (form == null) {
            throw new ValidationException("Edge data is required.");
        }
        if (form.getFromNodeId() == null && form.getToNodeId() == null) {
            throw new ValidationException("Edge cannot be both PUBLIC and PRIVATE.");
        }
        if (form.getFromNodeId() != null && form.getToNodeId() != null
            && form.getFromNodeId().equals(form.getToNodeId())) {
            throw new ValidationException("Self-loops are not allowed.");
        }
        if (form.getCreatedAt() != null && form.getExpiredAt() != null
            && form.getCreatedAt().isAfter(form.getExpiredAt())) {
            throw new ValidationException("Created time must be before expired time.");
        }
        if (form.getFromNodeId() != null && !nodeRepository.existsById(form.getFromNodeId())) {
            throw new ValidationException("From node not found.");
        }
        if (form.getToNodeId() != null && !nodeRepository.existsById(form.getToNodeId())) {
            throw new ValidationException("To node not found.");
        }
    }
}
