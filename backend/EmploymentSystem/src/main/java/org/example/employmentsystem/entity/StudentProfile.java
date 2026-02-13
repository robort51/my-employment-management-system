package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生档案实体类 - 对应 student_profile 表
 */
@Data
@TableName("student_profile")
public class StudentProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String realName;

    /** 性别：男/女 */
    private String gender;

    private String phone;

    private String email;

    /** 学校名称 */
    private String university;

    /** 专业 */
    private String major;

    /** 学历：专科/本科/硕士/博士 */
    private String education;

    /** 毕业年份 */
    private Integer graduationYear;

    /** 技能标签（逗号分隔） */
    private String skills;

    /** 自我介绍 */
    private String selfIntroduction;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
