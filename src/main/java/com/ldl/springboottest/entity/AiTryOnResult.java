package com.ldl.springboottest.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiTryOnResult {
    @JsonProperty("try_on_image")
    private String tryOnImage;
    
    private Evaluation evaluation;
    
    @JsonProperty("request_id")
    private String requestId;
    
    private String status;
    
    @Data
    public static class Evaluation {
        @JsonProperty("fit_score")
        private Double fitScore;
        
        @JsonProperty("style_match")
        private Double styleMatch;
        
        @JsonProperty("color_match")
        private Double colorMatch;
        
        private String suggestions;
    }
}
