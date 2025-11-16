package com.ldl.springboottest.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class AiTryOn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId; // 用户ID
    private String userNumber; // 用户编号
    private String faceImageUrl; // 人脸图片URL
    private Double height; // 身高（cm）
    private Double weight; // 体重（kg）
    private Double bust; // 胸围（cm）
    private Double waist; // 腰围（cm）
    private Double hips; // 臀围（cm）
    private Integer age; // 年龄
    private String gender; // 性别（男/女）
    private String commodityId; // 试穿商品ID
    private String tryOnImageUrl; // AI生成的试衣图片URL
    private String evaluation; // 客观评价
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private String status; // 状态（pending/processing/success/failed）
}
