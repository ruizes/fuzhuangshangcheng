package com.ldl.springboottest.repository;

import com.ldl.springboottest.entity.AiTryOn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AiTryOnRepository extends JpaRepository<AiTryOn, Integer> {

    // 根据用户编号查询试衣记录
    List<AiTryOn> findByUserNumber(String userNumber);

    // 根据商品ID查询试衣记录
    List<AiTryOn> findByCommodityId(String commodityId);

    // 根据用户编号和商品ID查询试衣记录
    List<AiTryOn> findByUserNumberAndCommodityId(String userNumber, String commodityId);

    // 根据状态查询试衣记录
    List<AiTryOn> findByStatus(String status);
}
