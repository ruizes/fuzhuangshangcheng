package com.ldl.springboottest.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AI试衣服务类
 */
@Service
public class AiTryOnService {

    @Value("${ai.try-on.model-url}")
    private String modelUrl;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * AI试衣功能
     * @param bodyData 用户身体数据
     * @param faceImage 用户人脸图
     * @param clothingImage 服装图片
     * @return 试衣结果，包含试衣图URL和评价
     */
    public Map<String, Object> tryOnClothing(Map<String, String> bodyData, MultipartFile faceImage, MultipartFile clothingImage) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 验证输入
            if (bodyData == null || bodyData.isEmpty()) {
                result.put("success", false);
                result.put("message", "身体数据不能为空");
                return result;
            }
            if (faceImage == null || faceImage.isEmpty()) {
                result.put("success", false);
                result.put("message", "人脸图不能为空");
                return result;
            }
            if (clothingImage == null || clothingImage.isEmpty()) {
                result.put("success", false);
                result.put("message", "服装图片不能为空");
                return result;
            }

            // 2. 保存上传的图片
            String faceImagePath = saveImage(faceImage, "face");
            String clothingImagePath = saveImage(clothingImage, "clothing");

            // 3. 调用AI试衣模型生成试衣图（这里简化处理，实际应该调用AI模型API）
            String tryOnImagePath = generateTryOnImage(bodyData, faceImagePath, clothingImagePath);

            // 4. 生成试衣评价
            String evaluation = generateEvaluation(bodyData, clothingImagePath);

            // 5. 构建结果
            result.put("success", true);
            result.put("tryOnImageUrl", "/uploads/" + tryOnImagePath.substring(tryOnImagePath.lastIndexOf("/") + 1));
            result.put("evaluation", evaluation);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "试衣失败，请稍后再试");
        }
        return result;
    }

    /**
     * 保存上传的图片
     * @param file 图片文件
     * @param type 图片类型（face或clothing）
     * @return 图片保存路径
     * @throws IOException IO异常
     */
    private String saveImage(MultipartFile file, String type) throws IOException {
        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String fileName = type + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destFile = new File(uploadDir, fileName);

        // 保存图片
        Thumbnails.of(file.getInputStream())
                .size(800, 800)
                .outputFormat("jpg")
                .toFile(destFile);

        return destFile.getAbsolutePath();
    }

    /**
     * 生成试衣图（简化实现，实际应该调用AI模型API）
     * @param bodyData 身体数据
     * @param faceImagePath 人脸图路径
     * @param clothingImagePath 服装图片路径
     * @return 试衣图路径
     * @throws IOException IO异常
     */
    private String generateTryOnImage(Map<String, String> bodyData, String faceImagePath, String clothingImagePath) throws IOException {
        // 这里简化处理，实际应该调用AI模型API生成试衣图
        // 现在只是将人脸图和服装图片简单合成
        BufferedImage faceImage = ImageIO.read(new File(faceImagePath));
        BufferedImage clothingImage = ImageIO.read(new File(clothingImagePath));

        // 创建新的图片
        int width = Math.max(faceImage.getWidth(), clothingImage.getWidth());
        int height = faceImage.getHeight() + clothingImage.getHeight();
        BufferedImage tryOnImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 绘制背景
        Graphics2D g2d = tryOnImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 绘制人脸图
        int faceX = (width - faceImage.getWidth()) / 2;
        g2d.drawImage(faceImage, faceX, 0, null);

        // 绘制服装图片
        int clothingX = (width - clothingImage.getWidth()) / 2;
        g2d.drawImage(clothingImage, clothingX, faceImage.getHeight(), null);

        g2d.dispose();

        // 保存试衣图
        String tryOnImageName = "try_on_" + System.currentTimeMillis() + ".jpg";
        File tryOnImageFile = new File(uploadPath, tryOnImageName);
        ImageIO.write(tryOnImage, "jpg", tryOnImageFile);

        return tryOnImageFile.getAbsolutePath();
    }

    /**
     * 生成试衣评价（简化实现，实际应该根据AI模型分析结果生成）
     * @param bodyData 身体数据
     * @param clothingImagePath 服装图片路径
     * @return 试衣评价
     */
    private String generateEvaluation(Map<String, String> bodyData, String clothingImagePath) {
        // 这里简化处理，实际应该根据AI模型分析结果生成评价
        String height = bodyData.get("height");
        String weight = bodyData.get("weight");
        String size = bodyData.get("size");

        StringBuilder evaluation = new StringBuilder();
        evaluation.append("根据您的身体数据（身高：").append(height).append("cm，体重：").append(weight).append("kg，尺码：").append(size).append("），");
        evaluation.append("这款服装的试穿效果如下：\n");
        evaluation.append("1. 尺码合适度：这款服装的尺码与您的身体数据匹配度较高，建议选择当前尺码。\n");
        evaluation.append("2. 风格匹配度：这款服装的风格与您的个人气质较为契合，能够展现您的优势。\n");
        evaluation.append("3. 颜色搭配：服装的颜色与您的肤色搭配协调，能够提升整体气色。\n");
        evaluation.append("4. 材质舒适度：服装采用的材质较为舒适，适合日常穿着。\n");
        evaluation.append("总体来说，这款服装非常适合您，建议购买。");

        return evaluation.toString();
    }
}