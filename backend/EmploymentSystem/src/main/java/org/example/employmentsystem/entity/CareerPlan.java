package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 职业规划 - 对应 career_plan 表
 */
@Data
@TableName("career_plan")
public class CareerPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生档案ID */
    private Long studentId;

    /** 规划内容 */
    private String planContent;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
