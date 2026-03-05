package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 简历实体类 - 对应 resume 表（一人一份）
 */
@Data
@TableName("resume")
public class Resume {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联学生档案ID */
    private Long studentId;

    /** 简历内容（富文本/纯文本） */
    private String content;

    /** 简历图片 URL */
    private String imageUrl;

    /** OCR 识别结果（供 AI 使用） */
    private String ocrText;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
