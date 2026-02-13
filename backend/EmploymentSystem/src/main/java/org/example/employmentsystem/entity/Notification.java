package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统通知实体类 - 对应 notification 表
 */
@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收用户ID */
    private Long userId;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 通知类型：apply-投递 interview-面试 audit-审核 system-系统 */
    private String type;

    /** 是否已读：0-未读 1-已读 */
    private Integer isRead;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
