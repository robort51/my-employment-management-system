package org.example.employmentsystem.service.impl;

import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextRequest;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponse;
import com.aliyun.tea.TeaException;
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
        String normalizedAk = normalizeCredential(accessKeyId);
        String normalizedSk = normalizeCredential(accessKeySecret);
        if (normalizedAk.isBlank() || normalizedSk.isBlank()) {
            throw new BusinessException("请先配置阿里云 OCR 密钥（aliyun.ocr.access-key-id / access-key-secret）");
        }

        try {
            String normalizedEndpoint = endpoint == null ? "" : endpoint.trim()
                    .replaceFirst("^https?://", "")
                    .replaceAll("/+$", "");
            if (normalizedEndpoint.isBlank()) {
                throw new BusinessException("aliyun.ocr.endpoint 配置为空");
            }

            Config config = new Config()
                    .setAccessKeyId(normalizedAk)
                    .setAccessKeySecret(normalizedSk)
                    .setEndpoint(normalizedEndpoint);
            Client client = new Client(config);

            RecognizeAllTextRequest request = new RecognizeAllTextRequest()
                    .setType(type)
                    .setBody(new ByteArrayInputStream(imageBytes));

            RecognizeAllTextResponse response = client.recognizeAllText(request);
            if (response == null || response.getBody() == null) {
                throw new BusinessException("OCR 识别失败：返回数据为空");
            }

            String code = response.getBody().getCode();
            if (code != null && !code.isBlank() && !"200".equals(code) && !"OK".equalsIgnoreCase(code)) {
                String msg = response.getBody().getMessage();
                throw new BusinessException("OCR 服务返回错误（code=" + code + "）: " + (msg == null ? "未知错误" : msg));
            }

            if (response.getBody().getData() == null) {
                throw new BusinessException("OCR 识别失败：未返回识别结果");
            }
            String content = response.getBody().getData().getContent();
            if (content == null || content.isBlank()) {
                throw new BusinessException("OCR 未识别到有效文字，请上传更清晰的简历图片");
            }
            return content;
        } catch (BusinessException e) {
            throw e;
        } catch (TeaException e) {
            log.error("调用阿里云 OCR 失败: code={}, status={}, message={}", e.getCode(), e.getStatusCode(), e.getMessage(), e);
            String code = e.getCode() == null ? "Unknown" : e.getCode();
            String msg = (e.getMessage() == null || e.getMessage().isBlank()) ? "未知错误" : e.getMessage();
            if ("SignatureDoesNotMatch".equals(code)) {
                throw new BusinessException("OCR 鉴权失败（SignatureDoesNotMatch）：请检查 AK/SK 是否成对、是否包含多余空格/引号、是否已在阿里云开通 OCR 统一识别");
            }
            throw new BusinessException("OCR 调用失败（" + code + "）: " + msg);
        } catch (Exception e) {
            log.error("调用阿里云 OCR 失败", e);
            throw new BusinessException("OCR 服务调用失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private String normalizeCredential(String value) {
        if (value == null) {
            return "";
        }
        String v = value.trim();
        if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
            v = v.substring(1, v.length() - 1).trim();
        }
        return v;
    }
}
