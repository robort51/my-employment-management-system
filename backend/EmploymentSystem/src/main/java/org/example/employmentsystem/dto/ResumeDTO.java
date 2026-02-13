package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 简历 DTO（一人一份简历）
 */
@Data
public class ResumeDTO {

    /** 简历内容（富文本/纯文本） */
    private String content;
}
