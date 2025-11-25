package com.ldl.springboottest.config;

import com.baidu.aip.nlp.AipNlp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI相关配置类
 */
@Configuration
public class AiConfig {

    // 百度AI配置
    @Value("${baidu.ai.app-id}")
    private String baiduAppId;

    @Value("${baidu.ai.api-key}")
    private String baiduApiKey;

    @Value("${baidu.ai.secret-key}")
    private String baiduSecretKey;

    /**
     * 创建百度AI NLP客户端
     */
    @Bean
    public AipNlp aipNlp() {
        AipNlp client = new AipNlp(baiduAppId, baiduApiKey, baiduSecretKey);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        return client;
    }
}