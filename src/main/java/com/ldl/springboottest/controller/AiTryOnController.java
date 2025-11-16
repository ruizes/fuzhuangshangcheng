package com.ldl.springboottest.controller;

import com.ldl.springboottest.entity.AiTryOn;
import com.ldl.springboottest.service.AiTryOnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/ai-try-on")
public class AiTryOnController {

    @Autowired
    private AiTryOnService aiTryOnService;

    // AI试衣接口
    @PostMapping("/generate")
    public ResponseEntity<?> generateTryOnImage(
            @RequestParam("faceImage") MultipartFile faceImage,
            @RequestParam("userNumber") String userNumber,
            @RequestParam("userId") String userId,
            @RequestParam("height") Double height,
            @RequestParam("weight") Double weight,
            @RequestParam("bust") Double bust,
            @RequestParam("waist") Double waist,
            @RequestParam("hips") Double hips,
            @RequestParam("age") Integer age,
            @RequestParam("gender") String gender,
            @RequestParam("commodityId") String commodityId) {
        try {
            AiTryOn result = aiTryOnService.generateTryOnImage(faceImage, userNumber, userId, height, weight, bust, waist, hips, age, gender, commodityId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("图片处理失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("AI试衣生成失败");
        }
    }

    // 根据用户编号获取试衣记录
    @GetMapping("/records/{userNumber}")
    public ResponseEntity<List<AiTryOn>> getTryOnRecords(@PathVariable String userNumber) {
        List<AiTryOn> records = aiTryOnService.getTryOnRecordsByUserNumber(userNumber);
        return ResponseEntity.ok(records);
    }

    // 根据ID获取试衣结果
    @GetMapping("/result/{id}")
    public ResponseEntity<AiTryOn> getTryOnResult(@PathVariable Integer id) {
        AiTryOn result = aiTryOnService.getTryOnResultById(id);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
