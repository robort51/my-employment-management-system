package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 面试结果反馈 DTO（企业填写面试结果）
 */
@Data
public class InterviewResultDTO {

    /** 面试结果：1-通过 2-未通过 */
    private Integer result;

    /** 面试反馈 */
    private String feedback;
}
