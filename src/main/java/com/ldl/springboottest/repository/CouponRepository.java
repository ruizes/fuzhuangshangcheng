package com.ldl.springboottest.repository;

import com.ldl.springboottest.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    /**
     * 乐观锁查询优惠券
     * @param couponId 优惠券ID
     * @return 优惠券信息
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.couponId = :couponId")
    Coupon findByCouponIdWithLock(@Param("couponId") Integer couponId);
}
