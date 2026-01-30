package com.example.graph.validate;

import com.example.graph.web.form.NodePublicForm;
import org.springframework.stereotype.Component;

@Component
public class NodePublicValidator {
    private static final int MAX_VALUE_LENGTH = 200;

    public void validate(NodePublicForm form) {
        if (form == null || form.getValue() == null || form.getValue().isBlank()) {
            throw new ValidationException("Node value is required.");
        }
        if (form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            throw new ValidationException("Node value must be at most 200 characters.");
        }
    }
}
