package org.example.employmentsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.ResumeDTO;
import org.example.employmentsystem.entity.Resume;
import org.example.employmentsystem.entity.StudentProfile;
import org.example.employmentsystem.service.ResumeService;
import org.example.employmentsystem.service.StudentProfileService;
import org.springframework.web.bind.annotation.*;

/**
 * 简历控制器（一人一份简历）
 */
@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final StudentProfileService studentProfileService;

    /**
     * 获取当前学生的 studentProfile ID
     */
    private Long getStudentId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        StudentProfile profile = studentProfileService.getByUserId(userId);
        if (profile == null) {
            throw new BusinessException("请先完善学生档案");
        }
        return profile.getId();
    }

    /**
     * 获取当前学生的简历
     * GET /api/resume
     */
    @GetMapping
    public Result<Resume> getResume(HttpServletRequest request) {
        Long studentId = getStudentId(request);
        Resume resume = resumeService.getByStudentId(studentId);
        return Result.success(resume);
    }

    /**
     * 保存或更新简历
     * POST /api/resume
     */
    @PostMapping
    public Result<?> saveResume(HttpServletRequest request,
                                @RequestBody ResumeDTO dto) {
        Long studentId = getStudentId(request);
        resumeService.saveOrUpdate(studentId, dto);
        return Result.success("简历保存成功", null);
    }

    /**
     * 根据学生档案ID获取简历（企业查看应聘者简历用）
     * GET /api/resume/{studentId}
     */
    @GetMapping("/{studentId}")
    public Result<Resume> getByStudentId(@PathVariable Long studentId) {
        Resume resume = resumeService.getByStudentId(studentId);
        return Result.success(resume);
    }
}
