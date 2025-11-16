package com.ldl.springboottest.service;

import com.ldl.springboottest.entity.KnowledgeBase;
import com.ldl.springboottest.repository.KnowledgeBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmartCustomerService {

    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;

    // 检索知识库，获取相关知识
    public List<KnowledgeBase> retrieveKnowledge(String query) {
        // 这里可以优化检索逻辑，比如使用更智能的关键词提取
        return knowledgeBaseRepository.searchByKeyword(query);
    }

    // 生成增强的提示词（将检索到的知识与用户问题结合）
    public String generateEnhancedPrompt(String userQuery, List<KnowledgeBase> knowledgeList) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是一个服装商城的智能客服，请根据以下知识库内容回答用户问题：\n\n");
        promptBuilder.append("知识库内容：\n");
        
        for (KnowledgeBase knowledge : knowledgeList) {
            promptBuilder.append("标题：").append(knowledge.getTitle()).append("\n");
            promptBuilder.append("内容：").append(knowledge.getContent()).append("\n\n");
        }
        
        promptBuilder.append("用户问题：").append(userQuery).append("\n");
        promptBuilder.append("要求：回答要准确、简洁，基于提供的知识库内容，不要添加额外信息。");
        
        return promptBuilder.toString();
    }

    // 调用大模型生成回答（这里需要根据实际使用的大模型API进行实现）
    public String generateAnswer(String enhancedPrompt) {
        // TODO: 集成实际的大模型API（如OpenAI、智谱、百度文心等）
        // 这里先返回模拟结果
        return "这是基于知识库的回答：" + enhancedPrompt;
    }

    // RAG完整流程
    public String getAnswer(String userQuery) {
        // 1. 检索知识库
        List<KnowledgeBase> knowledgeList = retrieveKnowledge(userQuery);
        // 2. 生成增强提示词
        String enhancedPrompt = generateEnhancedPrompt(userQuery, knowledgeList);
        // 3. 调用大模型生成回答
        String answer = generateAnswer(enhancedPrompt);
        // 4. 返回结果
        return answer;
    }
}
