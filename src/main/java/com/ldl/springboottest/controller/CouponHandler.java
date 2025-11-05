package com.ldl.springboottest.controller;

import com.ldl.springboottest.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/coupon")
public class CouponHandler {

    @Autowired
    private CouponService couponService;

    /**
     * 领取优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 领取结果
     */
    @PostMapping("/claim/{userId}/{couponId}")
    public String claimCoupon(@PathVariable Integer userId, @PathVariable Integer couponId) {
        boolean result = couponService.claimCoupon(userId, couponId);
        if (result) {
            return "success";
        } else {
            return "error";
        }
    }
}
