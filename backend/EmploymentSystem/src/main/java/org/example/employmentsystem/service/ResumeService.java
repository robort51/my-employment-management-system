package org.example.employmentsystem.service;

import org.example.employmentsystem.dto.ResumeDTO;
import org.example.employmentsystem.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    /** 获取学生简历 */
    Resume getByStudentId(Long studentId);

    /** 新增或更新简历（一人一份） */
    void saveOrUpdate(Long studentId, ResumeDTO dto);

    /** 上传简历图片并 OCR 识别 */
    Resume uploadImageAndRecognize(Long studentId, MultipartFile file);
}
