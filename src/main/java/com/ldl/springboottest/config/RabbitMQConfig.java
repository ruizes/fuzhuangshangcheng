package com.ldl.springboottest.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 优惠券秒杀队列
    public static final String COUPON_SECKILL_QUEUE = "coupon.seckill.queue";
    // 优惠券秒杀交换机
    public static final String COUPON_SECKILL_EXCHANGE = "coupon.seckill.exchange";
    // 优惠券秒杀路由键
    public static final String COUPON_SECKILL_ROUTING_KEY = "coupon.seckill.routing.key";
    
    // 死信队列
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";
    // 死信交换机
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";
    // 死信路由键
    public static final String DEAD_LETTER_ROUTING_KEY = "dead.letter.routing.key";

    // 声明死信交换机
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // 声明死信队列
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    // 绑定死信队列和死信交换机
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }

    // 声明优惠券秒杀交换机
    @Bean
    public DirectExchange couponSeckillExchange() {
        return new DirectExchange(COUPON_SECKILL_EXCHANGE);
    }

    // 声明优惠券秒杀队列，并设置死信交换机
    @Bean
    public Queue couponSeckillQueue() {
        return QueueBuilder.durable(COUPON_SECKILL_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // 消息过期时间60秒
                .build();
    }

    // 绑定优惠券秒杀队列和交换机
    @Bean
    public Binding couponSeckillBinding() {
        return BindingBuilder.bind(couponSeckillQueue())
                .to(couponSeckillExchange())
                .with(COUPON_SECKILL_ROUTING_KEY);
    }
}
