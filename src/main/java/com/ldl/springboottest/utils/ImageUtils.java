package com.ldl.springboottest.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImageUtils {
    /**
     * 将Base64字符串转换为图片字节数组
     * @param base64String Base64字符串
     * @return 图片字节数组
     */
    public static byte[] base64ToImage(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        
        // 去除Base64字符串中的前缀
        if (base64String.contains(",")) {
            base64String = base64String.split(",")[1];
        }
        
        return Base64.getDecoder().decode(base64String);
    }
    
    /**
     * 将图片字节数组转换为Base64字符串
     * @param imageBytes 图片字节数组
     * @return Base64字符串
     */
    public static String imageToBase64(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return null;
        }
        
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    
    /**
     * 压缩图片
     * @param imageBytes 原始图片字节数组
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return 压缩后的图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] compressImage(byte[] imageBytes, int targetWidth, int targetHeight) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        Thumbnails.of(inputStream)
                .size(targetWidth, targetHeight)
                .outputFormat("JPEG")
                .outputQuality(0.8)
                .toOutputStream(outputStream);
        
        return outputStream.toByteArray();
    }
    
    /**
     * 按比例压缩图片
     * @param imageBytes 原始图片字节数组
     * @param scale 压缩比例（0.1-1.0）
     * @return 压缩后的图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] compressImageByScale(byte[] imageBytes, double scale) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        Thumbnails.of(inputStream)
                .scale(scale)
                .outputFormat("JPEG")
                .outputQuality(0.8)
                .toOutputStream(outputStream);
        
        return outputStream.toByteArray();
    }
    
    /**
     * 获取图片格式
     * @param imageBytes 图片字节数组
     * @return 图片格式
     */
    public static String getImageFormat(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 4) {
            return null;
        }
        
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8) {
            return "JPEG";
        } else if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50 && imageBytes[2] == (byte) 0x4E && imageBytes[3] == (byte) 0x47) {
            return "PNG";
        } else if (imageBytes[0] == (byte) 0x47 && imageBytes[1] == (byte) 0x49 && imageBytes[2] == (byte) 0x46) {
            return "GIF";
        } else if (imageBytes[0] == (byte) 0x42 && imageBytes[1] == (byte) 0x4D) {
            return "BMP";
        }
        
        return null;
    }
    
    /**
     * 验证图片格式是否支持
     * @param imageBytes 图片字节数组
     * @return 是否支持
     */
    public static boolean isImageFormatSupported(byte[] imageBytes) {
        String format = getImageFormat(imageBytes);
        return format != null && ("JPEG".equals(format) || "PNG".equals(format) || "GIF".equals(format) || "BMP".equals(format));
    }
}
