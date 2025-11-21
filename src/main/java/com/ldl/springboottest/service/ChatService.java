package com.ldl.springboottest.service;

import com.ldl.springboottest.entity.KnowledgeBase;
import com.ldl.springboottest.repository.KnowledgeBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;
    
    public String getAnswer(String userQuery) {
        // 1. 使用RAG技术从知识库中检索相关信息
        List<KnowledgeBase> relevantKnowledge = knowledgeBaseRepository.searchByQuery(userQuery);
        
        // 2. 构建上下文
        String context = relevantKnowledge.stream()
                .map(kb -> kb.getQuestion() + ": " + kb.getAnswer())
                .collect(Collectors.joining("\n"));
        
        // 3. 调用大模型生成回答（这里使用模拟实现，实际应调用真实的大模型API）
        return generateAnswerWithLLM(userQuery, context);
    }
    
    private String generateAnswerWithLLM(String query, String context) {
        // 模拟大模型回答
        if (context.isEmpty()) {
            return "抱歉，我暂时无法回答这个问题，请您换个方式提问。";
        }
        
        return String.format("根据知识库信息，您的问题答案如下：\n\n%s\n\n如果您需要更详细的信息，请随时告诉我。", context);
    }
    
    public KnowledgeBase addKnowledge(KnowledgeBase knowledgeBase) {
        return knowledgeBaseRepository.save(knowledgeBase);
    }
    
    public List<KnowledgeBase> getAllKnowledge() {
        return knowledgeBaseRepository.findAll();
    }
}
