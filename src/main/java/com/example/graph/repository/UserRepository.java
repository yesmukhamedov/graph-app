package com.example.graph.repository;

import com.example.graph.model.user.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByNodeId(Long nodeId);

    boolean existsByNodeId(Long nodeId);
}
