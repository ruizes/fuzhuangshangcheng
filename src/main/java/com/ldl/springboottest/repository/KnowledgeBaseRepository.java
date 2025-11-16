package com.ldl.springboottest.repository;

import com.ldl.springboottest.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Integer> {

    // 根据关键词模糊查询知识库
    @Query(value = "SELECT * FROM knowledge_base WHERE is_active = 1 AND (title LIKE %:keyword% OR content LIKE %:keyword% OR keywords LIKE %:keyword%)", nativeQuery = true)
    List<KnowledgeBase> searchByKeyword(@Param("keyword") String keyword);

    // 根据分类查询知识库
    List<KnowledgeBase> findByCategoryAndIsActive(String category, Integer isActive);

    // 查询所有启用的知识库
    List<KnowledgeBase> findByIsActive(Integer isActive);
}
