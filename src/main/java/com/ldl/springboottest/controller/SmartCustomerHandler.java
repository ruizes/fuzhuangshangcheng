package com.ldl.springboottest.controller;

import com.ldl.springboottest.service.SmartCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能客服控制器
 */
@RestController
@RequestMapping("/api/customer-service")
public class SmartCustomerHandler {

    @Autowired
    private SmartCustomerService smartCustomerService;

    /**
     * 处理用户问题
     * @param request 请求体，包含question字段
     * @return 回答
     */
    @PostMapping("/ask")
    public Map<String, Object> askQuestion(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String question = request.get("question");
            if (question == null || question.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "问题不能为空");
                return response;
            }

            String answer = smartCustomerService.handleQuestion(question);
            response.put("success", true);
            response.put("answer", answer);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "处理失败，请稍后再试");
        }
        return response;
    }


}