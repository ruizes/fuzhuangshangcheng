package com.ldl.springboottest.service;

import com.ldl.springboottest.entity.Coupon;
import com.ldl.springboottest.entity.UserCoupon;
import com.ldl.springboottest.repository.CouponRepository;
import com.ldl.springboottest.repository.UserCouponRepository;
import com.ldl.springboottest.config.RabbitMQConfig;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class CouponService {

    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private UserCouponRepository userCouponRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedissonClient redissonClient;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    // 限流：每个用户每分钟最多领取5次优惠券
    private static final String LIMIT_KEY_PREFIX = "coupon:limit:";
    private static final long LIMIT_TIME_WINDOW = 60 * 1000;
    private static final int LIMIT_COUNT = 5;
    
    // 优惠券库存Redis键前缀
    private static final String STOCK_KEY_PREFIX = "coupon:stock:";

    /**
     * 领取优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 是否领取成功
     */
    public boolean claimCoupon(Integer userId, Integer couponId) {
        // 1. 检查优惠券是否存在且有效
        Coupon coupon = couponRepository.findById(couponId).orElse(null);
        if (coupon == null) {
            logger.error("Coupon not found: {}", couponId);
            return false;
        }
        
        Date now = new Date();
        if (now.before(coupon.getStartTime()) || now.after(coupon.getEndTime())) {
            logger.error("Coupon not available: {}", couponId);
            return false;
        }
        
        // 2. 检查用户是否已经领取过该优惠券
        UserCoupon existingUserCoupon = userCouponRepository.findByUserIdAndCouponId(userId, couponId);
        if (existingUserCoupon != null) {
            logger.error("User already claimed coupon: userId={}, couponId={}", userId, couponId);
            return false;
        }
        
        // 3. 限流防刷：检查用户领取次数
        String limitKey = LIMIT_KEY_PREFIX + userId;
        Long count = redisTemplate.opsForValue().increment(limitKey, 1);
        if (count == 1) {
            redisTemplate.expire(limitKey, LIMIT_TIME_WINDOW, TimeUnit.MILLISECONDS);
        } else if (count > LIMIT_COUNT) {
            logger.error("User claim limit exceeded: userId={}", userId);
            return false;
        }
        
        // 4. Redis预检查库存
        String stockKey = STOCK_KEY_PREFIX + couponId;
        Integer stock = (Integer) redisTemplate.opsForValue().get(stockKey);
        if (stock == null || stock <= 0) {
            logger.error("Coupon stock exhausted: {}", couponId);
            return false;
        }
        
        // 5. 获取Redisson公平锁
        RLock lock = redissonClient.getFairLock("coupon:lock:" + couponId);
        boolean isLocked = false;
        try {
            // 尝试获取锁，等待时间3秒，锁自动过期时间10秒（Redisson会自动续期）
            isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                logger.error("Failed to acquire lock for coupon: {}", couponId);
                return false;
            }
            
            // 6. 数据库双重检查库存
            Coupon couponWithLock = couponRepository.findByCouponIdWithLock(couponId);
            if (couponWithLock == null || couponWithLock.getRemainingQuantity() <= 0) {
                logger.error("Coupon stock exhausted (db check): {}", couponId);
                return false;
            }
            
            // 7. 扣减库存
            couponWithLock.setRemainingQuantity(couponWithLock.getRemainingQuantity() - 1);
            couponRepository.save(couponWithLock);
            
            // 8. 更新Redis库存
            redisTemplate.opsForValue().decrement(stockKey, 1);
            
            // 9. 创建用户优惠券记录
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(couponId);
            userCoupon.setStatus(1); // 未使用
            userCoupon.setReceiveTime(now);
            userCoupon.setExpireTime(coupon.getEndTime());
            userCoupon.setCreateTime(now);
            userCoupon.setUpdateTime(now);
            userCouponRepository.save(userCoupon);
            
            // 10. 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(RabbitMQConfig.COUPON_SECKILL_EXCHANGE, 
                                        RabbitMQConfig.COUPON_SECKILL_ROUTING_KEY, 
                                        userCoupon);
            
            logger.info("Coupon claimed successfully: userId={}, couponId={}", userId, couponId);
            return true;
            
        } catch (InterruptedException e) {
            logger.error("Lock acquisition interrupted: {}", couponId, e);
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }
}
