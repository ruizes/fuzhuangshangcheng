package com.ldl.springboottest.controller;

import com.ldl.springboottest.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 智能客服问答接口
     * @param question 用户问题
     * @return 回答
     */
    @PostMapping("/chat")
    public String chat(@RequestBody String question) {
        try {
            if (question == null || question.trim().isEmpty()) {
                return "请输入您的问题。";
            }
            return aiService.getEnhancedAnswer(question);
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，我现在无法回答您的问题，请稍后再试。";
        }
    }

    /**
     * AI试衣接口
     * @param bodyData 身体数据（JSON格式）
     * @param faceImage 人脸图片Base64编码
     * @param clothingId 服装ID
     * @return 试衣结果
     */
    @PostMapping("/try-on")
    public String tryOnClothing(
            @RequestParam String bodyData,
            @RequestParam String faceImage,
            @RequestParam Long clothingId) {
        try {
            if (bodyData == null || bodyData.trim().isEmpty()) {
                return "请输入您的身体数据。";
            }
            if (faceImage == null || faceImage.trim().isEmpty()) {
                return "请上传您的人脸图片。";
            }
            if (clothingId == null || clothingId <= 0) {
                return "请选择正确的服装。";
            }
            return aiService.tryOnClothing(bodyData, faceImage, clothingId);
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，试衣失败，请稍后再试。";
        }
    }
}