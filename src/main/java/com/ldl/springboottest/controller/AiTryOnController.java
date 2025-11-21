package com.ldl.springboottest.controller;

import com.ldl.springboottest.entity.AiTryOnRequest;
import com.ldl.springboottest.entity.AiTryOnResult;
import com.ldl.springboottest.service.AiTryOnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@CrossOrigin
@RequestMapping("/api/try-on")
public class AiTryOnController {
    @Autowired
    private AiTryOnService aiTryOnService;
    
    @PostMapping("/submit")
    public AiTryOnResult submitTryOnRequest(
            @RequestParam Integer userId,
            @RequestParam Double height,
            @RequestParam Double weight,
            @RequestParam String gender,
            @RequestParam Double bust,
            @RequestParam Double waist,
            @RequestParam Double hips,
            @RequestParam String skinColor,
            @RequestParam MultipartFile faceImage,
            @RequestParam Integer clothingId) throws IOException {
        
        AiTryOnRequest request = new AiTryOnRequest();
        request.setUserId(userId);
        request.setClothingId(clothingId);
        
        AiTryOnRequest.BodyData bodyData = new AiTryOnRequest.BodyData();
        bodyData.setHeight(height);
        bodyData.setWeight(weight);
        bodyData.setGender(gender);
        bodyData.setBust(bust);
        bodyData.setWaist(waist);
        bodyData.setHips(hips);
        bodyData.setSkinColor(skinColor);
        request.setBodyData(bodyData);
        
        // 将图片转换为Base64
        String base64Image = Base64.getEncoder().encodeToString(faceImage.getBytes());
        request.setFaceImage(base64Image);
        
        return aiTryOnService.processTryOnRequest(request);
    }
    
    @PostMapping("/submit-json")
    public AiTryOnResult submitTryOnRequestJson(@RequestBody AiTryOnRequest request) {
        return aiTryOnService.processTryOnRequest(request);
    }
}
