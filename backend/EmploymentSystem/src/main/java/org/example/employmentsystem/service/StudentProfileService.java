package org.example.employmentsystem.service;

import org.example.employmentsystem.dto.StudentProfileDTO;
import org.example.employmentsystem.entity.StudentProfile;

/**
 * 学生档案 Service 接口
 */
public interface StudentProfileService {

    /**
     * 获取当前学生的档案
     */
    StudentProfile getByUserId(Long userId);

    /**
     * 保存或更新学生档案（有则更新，无则新建）
     */
    void saveOrUpdate(Long userId, StudentProfileDTO dto);
}
