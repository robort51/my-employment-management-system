package org.example.employmentsystem.service.impl;

import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextRequest;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.service.OcrService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * 阿里云 OCR 实现
 */
@Slf4j
@Service
public class OcrServiceImpl implements OcrService {

    @Value("${aliyun.ocr.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.ocr.access-key-secret:}")
    private String accessKeySecret;

    @Value("${aliyun.ocr.endpoint:ocr-api.cn-hangzhou.aliyuncs.com}")
    private String endpoint;

    @Value("${aliyun.ocr.type:Advanced}")
    private String type;

    @Override
    public String recognizeText(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new BusinessException("图片内容不能为空");
        }
        if (accessKeyId.isBlank() || accessKeySecret.isBlank()) {
            throw new BusinessException("请先配置阿里云 OCR 密钥（aliyun.ocr.access-key-id / access-key-secret）");
        }

        try {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint(endpoint);
            Client client = new Client(config);

            RecognizeAllTextRequest request = new RecognizeAllTextRequest()
                    .setType(type)
                    .setBody(new ByteArrayInputStream(imageBytes));

            RecognizeAllTextResponse response = client.recognizeAllText(request);
            if (response == null || response.getBody() == null || response.getBody().getData() == null) {
                throw new BusinessException("OCR 识别失败：返回数据为空");
            }
            String content = response.getBody().getData().getContent();
            if (content == null || content.isBlank()) {
                throw new BusinessException("OCR 未识别到有效文字，请上传更清晰的简历图片");
            }
            return content;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用阿里云 OCR 失败", e);
            throw new BusinessException("OCR 服务调用失败，请稍后重试");
        }
    }
}

