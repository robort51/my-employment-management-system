package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.dto.StudentProfileDTO;
import org.example.employmentsystem.entity.StudentProfile;
import org.example.employmentsystem.mapper.StudentProfileMapper;
import org.example.employmentsystem.service.StudentProfileService;
import org.springframework.stereotype.Service;

/**
 * 学生档案 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileMapper studentProfileMapper;

    @Override
    public StudentProfile getByUserId(Long userId) {
        LambdaQueryWrapper<StudentProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentProfile::getUserId, userId);
        return studentProfileMapper.selectOne(wrapper);
    }

    @Override
    public void saveOrUpdate(Long userId, StudentProfileDTO dto) {
        StudentProfile profile = getByUserId(userId);

        if (profile == null) {
            // 新建档案
            profile = new StudentProfile();
            profile.setUserId(userId);
            copyDtoToEntity(dto, profile);
            studentProfileMapper.insert(profile);
        } else {
            // 更新档案
            copyDtoToEntity(dto, profile);
            studentProfileMapper.updateById(profile);
        }
    }

    /**
     * 将 DTO 字段复制到实体
     */
    private void copyDtoToEntity(StudentProfileDTO dto, StudentProfile profile) {
        profile.setRealName(dto.getRealName());
        profile.setGender(dto.getGender());
        profile.setPhone(dto.getPhone());
        profile.setEmail(dto.getEmail());
        profile.setUniversity(dto.getUniversity());
        profile.setMajor(dto.getMajor());
        profile.setEducation(dto.getEducation());
        profile.setGraduationYear(dto.getGraduationYear());
        profile.setSkills(dto.getSkills());
        profile.setSelfIntroduction(dto.getSelfIntroduction());
    }
}
