package com.example.graph.validate;

import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.PhonePatternRepository;
import com.example.graph.repository.PhoneValueRepository;
import com.example.graph.web.form.PhonePublicForm;
import org.springframework.stereotype.Component;

@Component
public class PhonePublicValidator {
    private static final int MAX_VALUE_LENGTH = 32;

    private final NodeRepository nodeRepository;
    private final PhonePatternRepository phonePatternRepository;
    private final PhoneValueRepository phoneValueRepository;

    public PhonePublicValidator(NodeRepository nodeRepository,
                                PhonePatternRepository phonePatternRepository,
                                PhoneValueRepository phoneValueRepository) {
        this.nodeRepository = nodeRepository;
        this.phonePatternRepository = phonePatternRepository;
        this.phoneValueRepository = phoneValueRepository;
    }

    public void validate(PhonePublicForm form) {
        if (form == null) {
            throw new ValidationException("Phone data is required.");
        }
        if (form.getNodeId() == null) {
            throw new ValidationException("Node is required.");
        }
        if (form.getPatternId() == null) {
            throw new ValidationException("Pattern is required.");
        }
        if (form.getValue() == null || form.getValue().isBlank()) {
            throw new ValidationException("Value is required.");
        }
        if (form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            throw new ValidationException("Value must be at most 32 characters.");
        }
        if (!nodeRepository.existsById(form.getNodeId())) {
            throw new ValidationException("Node not found.");
        }
        if (!phonePatternRepository.existsById(form.getPatternId())) {
            throw new ValidationException("Pattern not found.");
        }
        if (phoneValueRepository.existsByValue(form.getValue())) {
            throw new ValidationException("Phone value already exists.");
        }
    }
}
