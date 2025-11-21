package com.ldl.springboottest.service;

import com.ldl.springboottest.entity.AiTryOnRequest;
import com.ldl.springboottest.entity.AiTryOnResult;
import com.ldl.springboottest.entity.Commodity;
import com.ldl.springboottest.repository.CommodityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AiTryOnService {
    @Autowired
    @Qualifier("aiTryOnWebClient")
    private WebClient aiTryOnWebClient;
    
    @Autowired
    private CommodityRepository commodityRepository;
    
    public AiTryOnResult processTryOnRequest(AiTryOnRequest request) {
        // 1. 从数据库获取商品信息
        Commodity commodity = commodityRepository.findById(request.getClothingId()).orElse(null);
        if (commodity == null) {
            AiTryOnResult result = new AiTryOnResult();
            result.setStatus("error");
            result.setRequestId(UUID.randomUUID().toString());
            result.setTryOnImage("");
            AiTryOnResult.Evaluation evaluation = new AiTryOnResult.Evaluation();
            evaluation.setSuggestions("商品不存在");
            result.setEvaluation(evaluation);
            return result;
        }
        
        // 2. 调用AI试衣API（这里使用模拟实现，实际应调用真实的AI试衣服务）
        return generateMockResult(request, commodity);
    }
    
    private AiTryOnResult generateMockResult(AiTryOnRequest request, Commodity commodity) {
        AiTryOnResult result = new AiTryOnResult();
        result.setStatus("success");
        result.setRequestId(UUID.randomUUID().toString());
        
        // 模拟生成试衣图（实际应为AI生成的图片Base64）
        result.setTryOnImage("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/4QAiRXhpZgAATU0AKgAAAAgAAQESAAMAAAABAAEAAAAAAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDAREAAhEBAxEB/8QAFAABAAAAAAAAAAAAAAAAAAAACf/EABQQAQAAAAAAAAAAAAAAAAAAAAD/xAAUAQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwD3+iiigD//2Q==");
        
        // 生成客观评价
        AiTryOnResult.Evaluation evaluation = new AiTryOnResult.Evaluation();
        evaluation.setFitScore(ThreadLocalRandom.current().nextDouble(70, 95));
        evaluation.setStyleMatch(ThreadLocalRandom.current().nextDouble(75, 98));
        evaluation.setColorMatch(ThreadLocalRandom.current().nextDouble(80, 99));
        evaluation.setSuggestions("这件衣服很适合您的身材，颜色也很搭您的肤色。建议搭配一双白色运动鞋，效果会更好。");
        
        result.setEvaluation(evaluation);
        
        return result;
    }
}
