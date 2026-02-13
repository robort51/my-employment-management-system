package org.example.employmentsystem.service;

/**
 * AI 通用调用服务（对接 DeepSeek API）
 */
public interface AiService {

    /**
     * 发送消息给 AI 并获取回复
     *
     * @param systemPrompt 系统提示词（定义AI角色）
     * @param userMessage  用户消息
     * @return AI 回复内容
     */
    String chat(String systemPrompt, String userMessage);
}
