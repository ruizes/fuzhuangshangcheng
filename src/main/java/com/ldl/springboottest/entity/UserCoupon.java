package com.ldl.springboottest.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer userId;
    
    private Integer couponId;
    
    private Integer status;
    
    private Date receiveTime;
    
    private Date useTime;
    
    private Date expireTime;
    
    private Date createTime;
    
    private Date updateTime;
}