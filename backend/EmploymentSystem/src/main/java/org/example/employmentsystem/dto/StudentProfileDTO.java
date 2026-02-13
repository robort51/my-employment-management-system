package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 学生档案 - 保存/更新请求参数
 */
@Data
public class StudentProfileDTO {

    private String realName;
    private String gender;
    private String phone;
    private String email;
    private String university;
    private String major;
    private String education;
    private Integer graduationYear;
    private String skills;
    private String selfIntroduction;
}
