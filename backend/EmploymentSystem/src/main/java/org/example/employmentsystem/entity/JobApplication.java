package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 求职申请实体类 - 对应 job_application 表
 */
@Data
@TableName("job_application")
public class JobApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生档案ID */
    private Long studentId;

    /** 职位ID */
    private Long jobId;

    /** 状态：0-已投递 1-已查看 2-通过筛选 3-已拒绝 4-已邀面试 5-已录用 */
    private Integer status;

    /** 企业备注 */
    private String remark;

    /** 投递时间 */
    private LocalDateTime applyTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
