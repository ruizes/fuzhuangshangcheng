package com.ldl.springboottest.repository;

import com.ldl.springboottest.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Integer> {
    @Query(value = "SELECT * FROM knowledge_base WHERE question LIKE concat('%', :query, '%') OR answer LIKE concat('%', :query, '%')", nativeQuery = true)
    List<KnowledgeBase> searchByQuery(@Param("query") String query);
    
    List<KnowledgeBase> findByCategory(String category);
}
