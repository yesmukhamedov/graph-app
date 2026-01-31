package com.example.graph.repository;

import com.example.graph.model.NodeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> {
    @Query("""
        select n
        from NodeEntity n
        where n.id not in (select u.node.id from UserEntity u)
        """)
    List<NodeEntity> findNodesWithoutUser();
}
