package com.ldl.springboottest.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponId;
    
    private String couponName;
    
    private Integer totalQuantity;
    
    private Integer remainingQuantity;
    
    private Date startTime;
    
    private Date endTime;
    
    private Integer discountAmount;
    
    private Integer minOrderAmount;
    
    private Integer maxDiscountAmount;
    
    private Integer status;
    
    private Date createTime;
    
    private Date updateTime;
}