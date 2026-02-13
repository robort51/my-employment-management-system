package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 模拟面试答题 DTO
 */
@Data
public class AiMockAnswerDTO {

    /** 关联的模拟面试记录ID */
    private Long recordId;

    /** 学生的回答内容（JSON格式的问答数组） */
    private String answers;
}
