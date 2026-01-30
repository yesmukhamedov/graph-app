package com.example.graph.repository;

import com.example.graph.model.phone.PhonePatternEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhonePatternRepository extends JpaRepository<PhonePatternEntity, Long> {
}
