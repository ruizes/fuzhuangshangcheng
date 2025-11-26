package com.ldl.springboottest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    // 向量知识库
    private static final List<KnowledgeItem> VECTOR_KNOWLEDGE_BASE = new ArrayList<>();

    // 百度文心一言API配置
    private static final String BAIDU_API_KEY = "your_api_key_here"; // 请替换为您的百度文心一言API Key
    private static final String BAIDU_SECRET_KEY = "your_secret_key_here"; // 请替换为您的百度文心一言Secret Key
    private static final String BAIDU_ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String BAIDU_CHAT_API_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions";

    // 向量维度
    private static final int VECTOR_DIMENSION = 10;

    static {
        // 初始化向量知识库内容
        VECTOR_KNOWLEDGE_BASE.add(new KnowledgeItem("本店主要销售男装、女装、童装等各类服装，款式新颖，质量保证。", generateRandomVector(VECTOR_DIMENSION)));
        VECTOR_KNOWLEDGE_BASE.add(new KnowledgeItem("我们提供7天无理由退换货服务，退换货时请保持商品原样，附带购物小票。", generateRandomVector(VECTOR_DIMENSION)));
        VECTOR_KNOWLEDGE_BASE.add(new KnowledgeItem("本店支持多种支付方式，包括微信支付、支付宝支付、银行卡支付等。", generateRandomVector(VECTOR_DIMENSION)));
        VECTOR_KNOWLEDGE_BASE.add(new KnowledgeItem("商品配送时间一般为1-3天，偏远地区可能需要5-7天。", generateRandomVector(VECTOR_DIMENSION)));
        VECTOR_KNOWLEDGE_BASE.add(new KnowledgeItem("我们的客服热线是400-123-4567，工作时间为每天9:00-21:00。", generateRandomVector(VECTOR_DIMENSION)));
        VECTOR_KNOWLEDGE_BASE.add(new KnowledgeItem("本店经常举办促销活动，关注我们的公众号可以及时获取最新优惠信息。", generateRandomVector(VECTOR_DIMENSION)));
        VECTOR_KNOWLEDGE_BASE.add(new KnowledgeItem("商品尺码标准，请根据商品详情页的尺码表选择合适的尺码。", generateRandomVector(VECTOR_DIMENSION)));
        VECTOR_KNOWLEDGE_BASE.add(new KnowledgeItem("如果您对商品有任何疑问，可以随时联系我们的在线客服。", generateRandomVector(VECTOR_DIMENSION)));
    }

    /**
     * 生成随机向量（实际应用中应该使用预训练的词向量模型）
     * @param dimension 向量维度
     * @return 随机向量
     */
    private static RealVector generateRandomVector(int dimension) {
        double[] values = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            values[i] = Math.random() * 2 - 1; // 生成-1到1之间的随机数
        }
        return new ArrayRealVector(values);
    }

    /**
     * 计算两个向量的余弦相似度
     * @param v1 向量1
     * @param v2 向量2
     * @return 余弦相似度
     */
    private double calculateCosineSimilarity(RealVector v1, RealVector v2) {
        return v1.dotProduct(v2) / (v1.getNorm() * v2.getNorm());
    }

    /**
     * 向量知识库条目类
     */
    private static class KnowledgeItem {
        private String content;
        private RealVector vector;

        public KnowledgeItem(String content, RealVector vector) {
            this.content = content;
            this.vector = vector;
        }

        public String getContent() {
            return content;
        }

        public RealVector getVector() {
            return vector;
        }
    }

    /**
     * 使用RAG技术增强大模型回答
     * @param userQuestion 用户问题
     * @return 增强后的回答
     */
    public String getEnhancedAnswer(String userQuestion) {
        try {
            // 1. 从知识库中检索相关信息
            List<String> relevantInfo = retrieveRelevantInfo(userQuestion);

            // 2. 构建提示词
            String prompt = buildPrompt(userQuestion, relevantInfo);

            // 3. 调用大模型获取回答
            return callLargeLanguageModel(prompt);
        } catch (IOException e) {
            e.printStackTrace();
            return "抱歉，我现在无法回答您的问题，请稍后再试。";
        }
    }

    /**
     * 从向量知识库中检索相关信息
     * @param userQuestion 用户问题
     * @return 相关信息列表
     */
    private List<String> retrieveRelevantInfo(String userQuestion) {
        List<String> relevantInfo = new ArrayList<>();

        // 生成用户问题的向量（实际应用中应该使用预训练的词向量模型）
        RealVector questionVector = generateRandomVector(VECTOR_DIMENSION);

        // 计算用户问题向量与知识库中每个条目的相似度
        List<Map.Entry<KnowledgeItem, Double>> similarityList = new ArrayList<>();
        for (KnowledgeItem item : VECTOR_KNOWLEDGE_BASE) {
            double similarity = calculateCosineSimilarity(questionVector, item.getVector());
            similarityList.add(new HashMap.SimpleEntry<>(item, similarity));
        }

        // 按相似度降序排序
        similarityList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // 取相似度最高的前3个条目
        int count = 0;
        for (Map.Entry<KnowledgeItem, Double> entry : similarityList) {
            if (count >= 3) {
                break;
            }
            relevantInfo.add(entry.getKey().getContent());
            count++;
        }

        return relevantInfo;
    }

    /**
     * 构建提示词
     * @param userQuestion 用户问题
     * @param relevantInfo 相关信息
     * @return 提示词
     */
    private String buildPrompt(String userQuestion, List<String> relevantInfo) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下信息回答用户的问题：\n");
        prompt.append("用户问题：").append(userQuestion).append("\n");
        prompt.append("相关信息：\n");
        for (String info : relevantInfo) {
            prompt.append("- ").append(info).append("\n");
        }
        prompt.append("请用简洁明了的语言回答用户的问题，不要添加额外的信息。");
        return prompt.toString();
    }

    /**
     * 获取百度文心一言的访问令牌
     * @return 访问令牌
     * @throws IOException IO异常
     */
    private String getBaiduAccessToken() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(BAIDU_ACCESS_TOKEN_URL);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            String params = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s",
                    BAIDU_API_KEY, BAIDU_SECRET_KEY);
            httpPost.setEntity(new StringEntity(params));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "UTF-8");
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> map = objectMapper.readValue(result, Map.class);
                return (String) map.get("access_token");
            }
        }
        return null;
    }

    /**
     * 调用百度文心一言大模型API
     * @param prompt 提示词
     * @return 大模型回答
     * @throws IOException IO异常
     */
    private String callLargeLanguageModel(String prompt) throws IOException {
        String accessToken = getBaiduAccessToken();
        if (accessToken == null) {
            throw new IOException("Failed to get access token");
        }

        String apiUrl = BAIDU_CHAT_API_URL + "?access_token=" + accessToken;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");

            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2048);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "UTF-8");
                Map<String, Object> map = objectMapper.readValue(result, Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> messageResult = (Map<String, String>) choice.get("message");
                    return messageResult.get("content");
                }
            }
        }
        return "抱歉，我现在无法回答您的问题，请稍后再试。";
    }

    /**
     * AI试衣功能（调用阿里FashionAI API）
     * @param bodyData 身体数据（JSON格式）
     * @param faceImage 人脸图片Base64编码
     * @param clothingId 服装ID
     * @return 试衣结果和评价
     */
    public String tryOnClothing(String bodyData, String faceImage, Long clothingId) {
        try {
            // 阿里FashionAI API配置（示例）
            String fashionAIApiUrl = "https://api-fashion.aliyun.com/tryon";
            String fashionAIAppKey = "your_fashionai_app_key_here";
            String fashionAIAppSecret = "your_fashionai_app_secret_here";

            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("body_data", bodyData);
            requestBody.put("face_image", faceImage);
            requestBody.put("clothing_id", clothingId);
            requestBody.put("app_key", fashionAIAppKey);
            requestBody.put("timestamp", System.currentTimeMillis());

            // 生成签名（示例，实际签名算法需要根据阿里FashionAI API文档实现）
            String signature = generateFashionAISignature(requestBody, fashionAIAppSecret);
            requestBody.put("signature", signature);

            // 调用阿里FashionAI API
            String response = callFashionAIApi(fashionAIApiUrl, requestBody);

            // 解析API响应
            return parseFashionAIResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，试衣失败，请稍后再试。";
        }
    }

    /**
     * 生成阿里FashionAI API签名（示例实现，实际需要根据API文档调整）
     * @param requestBody 请求参数
     * @param appSecret 应用密钥
     * @return 签名
     */
    private String generateFashionAISignature(Map<String, Object> requestBody, String appSecret) {
        // 示例签名算法：将请求参数按字典序排序，拼接成字符串，然后使用appSecret进行MD5加密
        List<String> keys = new ArrayList<>(requestBody.keySet());
        keys.sort(String::compareTo);

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if (!"signature".equals(key)) {
                sb.append(key).append("=").append(requestBody.get(key)).append("&");
            }
        }
        sb.append("app_secret=").append(appSecret);

        // 这里使用简单的字符串哈希作为示例，实际应用中需要使用MD5或其他加密算法
        return Integer.toHexString(sb.toString().hashCode());
    }

    /**
     * 调用阿里FashionAI API
     * @param apiUrl API地址
     * @param requestBody 请求参数
     * @return API响应
     * @throws IOException IO异常
     */
    private String callFashionAIApi(String apiUrl, Map<String, Object> requestBody) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        }
        return null;
    }

    /**
     * 解析阿里FashionAI API响应
     * @param response API响应
     * @return 格式化的试衣结果
     * @throws IOException IO异常
     */
    private String parseFashionAIResponse(String response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);

        StringBuilder result = new StringBuilder();
        result.append("AI试衣结果：\n");

        // 解析试衣图片URL
        if (responseMap.containsKey("tryon_image")) {
            result.append("- 试衣图片：").append(responseMap.get("tryon_image")).append("\n");
        }

        // 解析试衣评价
        if (responseMap.containsKey("evaluation")) {
            result.append("- 评价：").append(responseMap.get("evaluation")).append("\n");
        }

        // 解析尺码建议
        if (responseMap.containsKey("size_suggestion")) {
            result.append("- 尺码建议：").append(responseMap.get("size_suggestion")).append("\n");
        }

        // 解析搭配建议
        if (responseMap.containsKey("matching_suggestion")) {
            result.append("- 搭配建议：").append(responseMap.get("matching_suggestion")).append("\n");
        }

        return result.toString();
    }
}