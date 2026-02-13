package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 简历润色请求 DTO
 */
@Data
public class AiResumePolishDTO {

    /** 需要润色的简历内容 */
    private String content;
}
