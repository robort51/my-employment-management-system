package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI简历润色记录 - 对应 ai_resume_record 表
 */
@Data
@TableName("ai_resume_record")
public class AiResumeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生档案ID */
    private Long studentId;

    /** 原始简历内容 */
    private String originalContent;

    /** AI润色后的内容 */
    private String polishedContent;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
