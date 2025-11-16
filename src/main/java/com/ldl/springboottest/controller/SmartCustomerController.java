package com.ldl.springboottest.controller;

import com.ldl.springboottest.service.SmartCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/smart-customer")
public class SmartCustomerController {

    @Autowired
    private SmartCustomerService smartCustomerService;

    // 智能客服问答接口
    @PostMapping("/ask")
    public String askQuestion(@RequestBody String userQuery) {
        try {
            String answer = smartCustomerService.getAnswer(userQuery);
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，我暂时无法回答您的问题，请稍后重试。";
        }
    }
}
