package com.ldl.springboottest;

import com.ldl.springboottest.entity.Coupon;
import com.ldl.springboottest.repository.CouponRepository;
import com.ldl.springboottest.service.CouponService;
import com.ldl.springboottest.utils.PasswordEncoderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class CouponSeckillTest {

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private CouponService couponService;

    @Test
    public void testPasswordEncryption() {
        // 测试密码加密
        String rawPassword = "Test@1234";
        String encodedPassword = passwordEncoderUtil.encodePassword(rawPassword);
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
        
        // 测试密码匹配
        boolean matches = passwordEncoderUtil.matches(rawPassword, encodedPassword);
        System.out.println("Password matches: " + matches);
        
        // 测试密码强度校验
        String weakPassword = "123456";
        boolean isStrong = passwordEncoderUtil.isPasswordStrong(weakPassword);
        System.out.println("Weak password is strong: " + isStrong);
        
        String strongPassword = "Strong@123";
        isStrong = passwordEncoderUtil.isPasswordStrong(strongPassword);
        System.out.println("Strong password is strong: " + isStrong);
    }

    @Test
    public void testCouponSeckill() {
        // 创建测试优惠券
        Coupon coupon = new Coupon();
        coupon.setCouponName("Test Coupon");
        coupon.setTotalQuantity(100);
        coupon.setRemainingQuantity(100);
        coupon.setStartTime(new Date());
        Date endTime = new Date();
        endTime.setTime(endTime.getTime() + 24 * 60 * 60 * 1000); // 有效期1天
        coupon.setEndTime(endTime);
        coupon.setDiscountAmount(50);
        coupon.setMinOrderAmount(200);
        coupon.setMaxDiscountAmount(50);
        coupon.setStatus(1);
        coupon.setCreateTime(new Date());
        coupon.setUpdateTime(new Date());
        
        Coupon savedCoupon = couponRepository.save(coupon);
        System.out.println("Created coupon: " + savedCoupon);
        
        // 模拟100个用户同时领取优惠券
        for (int i = 1; i <= 100; i++) {
            int userId = i;
            new Thread(() -> {
                boolean result = couponService.claimCoupon(userId, savedCoupon.getCouponId());
                System.out.println("User " + userId + " claimed coupon: " + result);
            }).start();
        }
        
        // 等待所有线程完成
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 检查最终库存
        Coupon finalCoupon = couponRepository.findById(savedCoupon.getCouponId()).orElse(null);
        System.out.println("Final remaining quantity: " + (finalCoupon != null ? finalCoupon.getRemainingQuantity() : "Coupon not found"));
    }
}
