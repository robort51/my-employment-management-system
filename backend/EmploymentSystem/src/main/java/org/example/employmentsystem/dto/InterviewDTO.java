package org.example.employmentsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业发起面试邀请 DTO
 */
@Data
public class InterviewDTO {

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
}
