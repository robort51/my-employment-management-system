package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI模拟面试记录 - 对应 ai_interview_record 表
 */
@Data
@TableName("ai_interview_record")
public class AiInterviewRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生档案ID */
    private Long studentId;

    /** 目标职位名称 */
    private String jobTitle;

    /** 问答内容（JSON格式） */
    private String qaContent;

    /** AI综合评价 */
    private String aiFeedback;

    /** AI评分（0-100） */
    private Integer score;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
