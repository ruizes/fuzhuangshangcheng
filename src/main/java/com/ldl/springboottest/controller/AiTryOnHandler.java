package com.ldl.springboottest.controller;

import com.ldl.springboottest.service.AiTryOnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * AI试衣控制器
 */
@RestController
@RequestMapping("/api/ai-try-on")
public class AiTryOnHandler {

    @Autowired
    private AiTryOnService aiTryOnService;

    /**
     * AI试衣功能
     * @param height 身高
     * @param weight 体重
     * @param size 尺码
     * @param bust 胸围
     * @param waist 腰围
     * @param hips 臀围
     * @param faceImage 人脸图
     * @param clothingImage 服装图片
     * @return 试衣结果
     */
    @PostMapping("/try-on")
    public Map<String, Object> tryOnClothing(
            @RequestParam String height,
            @RequestParam String weight,
            @RequestParam String size,
            @RequestParam(required = false) String bust,
            @RequestParam(required = false) String waist,
            @RequestParam(required = false) String hips,
            @RequestParam MultipartFile faceImage,
            @RequestParam MultipartFile clothingImage) {

        // 构建身体数据Map
        Map<String, String> bodyData = new HashMap<>();
        bodyData.put("height", height);
        bodyData.put("weight", weight);
        bodyData.put("size", size);
        if (bust != null) bodyData.put("bust", bust);
        if (waist != null) bodyData.put("waist", waist);
        if (hips != null) bodyData.put("hips", hips);

        // 调用AI试衣服务
        return aiTryOnService.tryOnClothing(bodyData, faceImage, clothingImage);
    }
}