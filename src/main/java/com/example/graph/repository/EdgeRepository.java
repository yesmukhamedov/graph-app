package com.example.graph.repository;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NameEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeRepository extends JpaRepository<EdgeEntity, Long> {
    boolean existsByFromNodeIdAndToNodeId(Long fromId, Long toId);

    List<EdgeEntity> findAllByFromNodeIsNull();

    List<EdgeEntity> findAllByFromNodeId(Long fromId);

    @Query("""
        select distinct e.label
        from EdgeEntity e
        where e.fromNode is null
          and e.label is not null
        """)
    List<NameEntity> findDistinctPublicEdgeLabels();
}
