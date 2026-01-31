package com.example.graph.service.user;

import com.example.graph.model.user.ProfileEntity;
import com.example.graph.model.user.UserEntity;
import com.example.graph.repository.ProfileRepository;
import com.example.graph.repository.UserRepository;
import com.example.graph.validate.ValidationException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public boolean existsByPhoneDigits(String phoneDigits) {
        return profileRepository.existsByPhoneDigits(phoneDigits);
    }

    public ProfileEntity createCurrentProfile(UserEntity user, String phoneDigits, OffsetDateTime createdAt) {
        ProfileEntity profile = new ProfileEntity();
        profile.setUser(user);
        profile.setPhoneDigits(phoneDigits);
        profile.setCreatedAt(createdAt);
        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public Optional<ProfileEntity> getCurrentProfile(UUID userId, OffsetDateTime now) {
        return profileRepository.findCurrentProfileByUserId(userId, now);
    }

    @Transactional(readOnly = true)
    public Map<UUID, ProfileEntity> getCurrentProfiles(OffsetDateTime now) {
        return profileRepository.findCurrentProfiles(now).stream()
            .collect(Collectors.toMap(value -> value.getUser().getId(), value -> value, (a, b) -> a));
    }

    @Transactional
    public ProfileEntity versionPhoneDigits(UUID userId, String phoneDigits, OffsetDateTime effectiveAt) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ValidationException("User not found."));
        ProfileEntity current = profileRepository.findCurrentProfileByUserId(userId, effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            profileRepository.save(current);
        }
        ProfileEntity next = new ProfileEntity();
        next.setUser(user);
        next.setPhoneDigits(phoneDigits);
        next.setCreatedAt(effectiveAt);
        return profileRepository.save(next);
    }
}
