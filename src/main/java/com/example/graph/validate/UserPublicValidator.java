package com.example.graph.validate;

import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.ProfileRepository;
import com.example.graph.web.form.UserPublicForm;
import org.springframework.stereotype.Component;

@Component
public class UserPublicValidator {
    private final NodeRepository nodeRepository;
    private final ProfileRepository profileRepository;
    private final ProfileDigitsValidator profileDigitsValidator;

    public UserPublicValidator(NodeRepository nodeRepository,
                               ProfileRepository profileRepository,
                               ProfileDigitsValidator profileDigitsValidator) {
        this.nodeRepository = nodeRepository;
        this.profileRepository = profileRepository;
        this.profileDigitsValidator = profileDigitsValidator;
    }

    public void validate(UserPublicForm form, String fieldPrefix, ValidationErrorCollector errors) {
        if (form == null) {
            errors.add(fieldPrefix, "User data is required.");
            return;
        }
        if (form.getNodeId() == null) {
            errors.add(fieldPrefix + ".nodeId", "Node is required.");
        } else if (!nodeRepository.existsById(form.getNodeId())) {
            errors.add(fieldPrefix + ".nodeId", "Node not found.");
        }
        String normalized = form.getValue() == null ? null : form.getValue().trim();
        if (normalized == null || normalized.isBlank()) {
            errors.add(fieldPrefix + ".value", "Phone digits are required.");
        } else {
            profileDigitsValidator.validateDigits(normalized, fieldPrefix + ".value", errors);
        }
        if (normalized != null && !normalized.isBlank() && profileRepository.existsByPhoneDigits(normalized)) {
            errors.add(fieldPrefix + ".value", "Phone digits already exist.");
        }
    }
}
