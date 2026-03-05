package org.example.employmentsystem.service;

/**
 * OCR 识别服务
 */
public interface OcrService {

    /**
     * 识别图片中的文字内容
     */
    String recognizeText(byte[] imageBytes);
}

