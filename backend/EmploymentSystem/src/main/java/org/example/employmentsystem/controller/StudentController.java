package org.example.employmentsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.StudentProfileDTO;
import org.example.employmentsystem.entity.StudentProfile;
import org.example.employmentsystem.service.StudentProfileService;
import org.springframework.web.bind.annotation.*;

/**
 * 学生档案控制器
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentProfileService studentProfileService;

    /**
     * 获取当前学生的档案
     * GET /api/student/profile
     */
    @GetMapping("/profile")
    public Result<StudentProfile> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        StudentProfile profile = studentProfileService.getByUserId(userId);
        return Result.success(profile);
    }

    /**
     * 保存或更新学生档案
     * POST /api/student/profile
     */
    @PostMapping("/profile")
    public Result<?> saveProfile(HttpServletRequest request,
                                 @RequestBody StudentProfileDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        studentProfileService.saveOrUpdate(userId, dto);
        return Result.success("档案保存成功", null);
    }
}
