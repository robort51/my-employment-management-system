package org.example.employmentsystem.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * DeepSeek AI 调用实现
 */
@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.base-url}")
    private String baseUrl;

    @Value("${ai.deepseek.model}")
    private String model;

    @Override
    public String chat(String systemPrompt, String userMessage) {
        if ("your-api-key-here".equals(apiKey)) {
            throw new BusinessException("请先配置 DeepSeek API Key（application.properties 中的 ai.deepseek.api-key）");
        }

        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.set("model", model);
            requestBody.set("temperature", 0.7);
            requestBody.set("max_tokens", 4096);

            JSONArray messages = new JSONArray();
            // system 消息
            JSONObject systemMsg = new JSONObject();
            systemMsg.set("role", "system");
            systemMsg.set("content", systemPrompt);
            messages.add(systemMsg);
            // user 消息
            JSONObject userMsg = new JSONObject();
            userMsg.set("role", "user");
            userMsg.set("content", userMessage);
            messages.add(userMsg);

            requestBody.set("messages", messages);

            // 发送 HTTP 请求
            String responseStr = HttpRequest.post(baseUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(60000) // 60秒超时
                    .execute()
                    .body();

            // 解析响应
            JSONObject response = JSONUtil.parseObj(responseStr);

            // 检查是否有错误
            if (response.containsKey("error")) {
                String errorMsg = response.getJSONObject("error").getStr("message", "未知错误");
                log.error("DeepSeek API 错误: {}", errorMsg);
                throw new BusinessException("AI 服务异常: " + errorMsg);
            }

            // 提取 AI 回复内容
            return response.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败", e);
            throw new BusinessException("AI 服务调用失败，请稍后重试");
        }
    }
}
