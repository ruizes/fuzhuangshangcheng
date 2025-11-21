package com.ldl.springboottest.entity;

import lombok.Data;

@Data
public class AiTryOnRequest {
    private Integer userId;
    private BodyData bodyData;
    private String faceImage;
    private Integer clothingId;
    
    @Data
    public static class BodyData {
        private Double height;
        private Double weight;
        private String gender;
        private Double bust;
        private Double waist;
        private Double hips;
        private String skinColor;
    }
}
