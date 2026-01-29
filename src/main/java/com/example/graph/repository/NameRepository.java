package com.example.graph.repository;

import com.example.graph.model.NameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NameRepository extends JpaRepository<NameEntity, Long> {
}
