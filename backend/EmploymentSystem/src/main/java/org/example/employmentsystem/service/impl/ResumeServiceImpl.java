package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.dto.ResumeDTO;
import org.example.employmentsystem.entity.Resume;
import org.example.employmentsystem.mapper.ResumeMapper;
import org.example.employmentsystem.service.ResumeService;
import org.springframework.stereotype.Service;

/**
 * 简历 Service 实现类（一人一份简历）
 */
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;

    @Override
    public Resume getByStudentId(Long studentId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getStudentId, studentId);
        return resumeMapper.selectOne(wrapper);
    }

    @Override
    public void saveOrUpdate(Long studentId, ResumeDTO dto) {
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("简历内容不能为空");
        }
        Resume resume = getByStudentId(studentId);
        if (resume == null) {
            resume = new Resume();
            resume.setStudentId(studentId);
            resume.setContent(dto.getContent());
            resumeMapper.insert(resume);
        } else {
            resume.setContent(dto.getContent());
            resumeMapper.updateById(resume);
        }
    }
}
