package com.ldl.springboottest.repository;

import com.ldl.springboottest.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {

    /**
     * 检查用户是否已经领取过该优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 用户优惠券信息
     */
    UserCoupon findByUserIdAndCouponId(Integer userId, Integer couponId);
}
