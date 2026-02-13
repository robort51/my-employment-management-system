package org.example.employmentsystem.service;

import org.example.employmentsystem.dto.ResumeDTO;
import org.example.employmentsystem.entity.Resume;

public interface ResumeService {

    /** 获取学生简历 */
    Resume getByStudentId(Long studentId);

    /** 新增或更新简历（一人一份） */
    void saveOrUpdate(Long studentId, ResumeDTO dto);
}
