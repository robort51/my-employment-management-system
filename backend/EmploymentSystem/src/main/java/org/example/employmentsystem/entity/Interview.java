package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 面试邀约实体类 - 对应 interview 表
 */
@Data
@TableName("interview")
public class Interview {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联求职申请ID */
    private Long applicationId;

    /** 面试时间 */
    private LocalDateTime interviewTime;

    /** 面试地点 */
    private String location;

    /** 面试方式：线上/线下 */
    private String type;

    /** 线上面试链接 */
    private String meetingLink;

    /** 面试备注/要求 */
    private String remark;

    /** 状态：0-待确认 1-已接受 2-已拒绝 3-已完成 4-已取消 */
    private Integer status;

    /** 面试结果：1-通过 2-未通过 */
    private Integer result;

    /** 面试反馈 */
    private String feedback;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
