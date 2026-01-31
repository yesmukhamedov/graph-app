package com.example.graph.service.user;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.user.ProfileEntity;
import com.example.graph.model.user.UserEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.UserRepository;
import com.example.graph.service.value.NodeValueService;
import com.example.graph.validate.ProfileDigitsValidator;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.dto.UserDto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ProfileService profileService;
    private final NodeValueService nodeValueService;
    private final ProfileDigitsValidator profileDigitsValidator;

    public UserService(UserRepository userRepository,
                       NodeRepository nodeRepository,
                       ProfileService profileService,
                       NodeValueService nodeValueService,
                       ProfileDigitsValidator profileDigitsValidator) {
        this.userRepository = userRepository;
        this.nodeRepository = nodeRepository;
        this.profileService = profileService;
        this.nodeValueService = nodeValueService;
        this.profileDigitsValidator = profileDigitsValidator;
    }

    public UserEntity createUserForNode(Long nodeId, String phoneDigits) {
        if (nodeId == null) {
            throw new IllegalArgumentException("Node is required.");
        }
        if (phoneDigits == null || phoneDigits.isBlank()) {
            throw new IllegalArgumentException("Phone digits are required.");
        }
        String normalizedDigits = phoneDigits.trim();
        profileDigitsValidator.validateDigits(normalizedDigits);
        NodeEntity node = nodeRepository.findById(nodeId)
            .orElseThrow(() -> new IllegalArgumentException("Node not found."));
        if (userRepository.existsByNodeId(nodeId)) {
            throw new IllegalArgumentException("Selected node already has a user.");
        }
        if (profileService.existsByPhoneDigits(normalizedDigits)) {
            throw new IllegalArgumentException("Phone digits already exist.");
        }
        OffsetDateTime now = OffsetDateTime.now();
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setNode(node);
        user.setCreatedAt(now);
        UserEntity savedUser = userRepository.save(user);
        profileService.createCurrentProfile(savedUser, normalizedDigits, now);
        return savedUser;
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserDto> listUsersDto() {
        OffsetDateTime now = OffsetDateTime.now();
        Map<Long, String> nodeNames = nodeValueService.getCurrentValues(now);
        Map<UUID, ProfileEntity> profiles = profileService.getCurrentProfiles(now);
        return userRepository.findAll().stream()
            .map(user -> {
                ProfileEntity profile = profiles.get(user.getId());
                return new UserDto(
                    user.getId(),
                    nodeNames.getOrDefault(user.getNode().getId(), "—"),
                    profile == null ? "—" : profile.getPhoneDigits()
                );
            })
            .toList();
    }

    public UserEntity getUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ValidationException("User not found."));
    }
}
