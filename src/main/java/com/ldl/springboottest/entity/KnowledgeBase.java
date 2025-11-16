package com.ldl.springboottest.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class KnowledgeBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title; // 知识标题
    private String content; // 知识内容
    private String category; // 分类（如商品信息、FAQ、售后政策等）
    private String keywords; // 关键词，用于检索
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private Integer isActive; // 是否启用（1：启用，0：禁用）
}