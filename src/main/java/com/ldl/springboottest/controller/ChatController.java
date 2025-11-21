package com.ldl.springboottest.controller;

import com.ldl.springboottest.entity.KnowledgeBase;
import com.ldl.springboottest.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    
    @PostMapping("/send")
    public String sendMessage(@RequestBody Map<String, String> request) {
        String userQuery = request.get("message");
        return chatService.getAnswer(userQuery);
    }
    
    @PostMapping("/knowledge")
    public KnowledgeBase addKnowledge(@RequestBody KnowledgeBase knowledgeBase) {
        return chatService.addKnowledge(knowledgeBase);
    }
    
    @GetMapping("/knowledge")
    public List<KnowledgeBase> getAllKnowledge() {
        return chatService.getAllKnowledge();
    }
}
