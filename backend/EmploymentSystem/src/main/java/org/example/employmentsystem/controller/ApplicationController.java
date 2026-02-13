package org.example.employmentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.ApplicationHandleDTO;
import org.example.employmentsystem.entity.CompanyProfile;
import org.example.employmentsystem.entity.JobApplication;
import org.example.employmentsystem.entity.StudentProfile;
import org.example.employmentsystem.service.CompanyProfileService;
import org.example.employmentsystem.service.JobApplicationService;
import org.example.employmentsystem.service.StudentProfileService;
import org.springframework.web.bind.annotation.*;

/**
 * 求职申请控制器
 */
@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final JobApplicationService jobApplicationService;
    private final StudentProfileService studentProfileService;
    private final CompanyProfileService companyProfileService;

    /** 获取学生档案ID */
    private Long getStudentId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        StudentProfile profile = studentProfileService.getByUserId(userId);
        if (profile == null) {
            throw new BusinessException("请先完善学生档案");
        }
        return profile.getId();
    }

    /** 获取企业档案ID */
    private Long getCompanyId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CompanyProfile profile = companyProfileService.getByUserId(userId);
        if (profile == null) {
            throw new BusinessException("请先完善企业档案");
        }
        return profile.getId();
    }

    // ===================== 学生端 =====================

    /**
     * 学生投递简历
     * POST /api/application/apply?jobId=1
     */
    @PostMapping("/apply")
    public Result<?> apply(HttpServletRequest request,
                           @RequestParam Long jobId) {
        Long studentId = getStudentId(request);
        jobApplicationService.apply(studentId, jobId);
        return Result.success("投递成功", null);
    }

    /**
     * 学生查看自己的投递记录
     * GET /api/application/my?pageNum=1&pageSize=10
     */
    @GetMapping("/my")
    public Result<IPage<JobApplication>> myApplications(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long studentId = getStudentId(request);
        IPage<JobApplication> page = jobApplicationService.getStudentApplications(studentId, pageNum, pageSize);
        return Result.success(page);
    }

    // ===================== 企业端 =====================

    /**
     * 企业查看收到的所有申请（通过企业下所有职位汇总）
     * GET /api/application/company?pageNum=1&pageSize=10
     */
    @GetMapping("/company")
    public Result<IPage<JobApplication>> companyApplications(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long companyId = getCompanyId(request);
        IPage<JobApplication> page = jobApplicationService.getCompanyApplications(companyId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 企业查看某职位收到的申请
     * GET /api/application/job/{jobId}?pageNum=1&pageSize=10
     */
    @GetMapping("/job/{jobId}")
    public Result<IPage<JobApplication>> jobApplications(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<JobApplication> page = jobApplicationService.getJobApplications(jobId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 企业处理申请（查看/通过筛选/拒绝/录用）
     * PUT /api/application/handle/{id}
     */
    @PutMapping("/handle/{id}")
    public Result<?> handle(HttpServletRequest request,
                            @PathVariable Long id,
                            @RequestBody ApplicationHandleDTO dto) {
        Long companyId = getCompanyId(request);
        jobApplicationService.handle(id, companyId, dto.getStatus(), dto.getRemark());
        return Result.success("处理成功", null);
    }

    /**
     * 获取申请详情
     * GET /api/application/{id}
     */
    @GetMapping("/{id}")
    public Result<JobApplication> detail(@PathVariable Long id) {
        JobApplication app = jobApplicationService.getById(id);
        return Result.success(app);
    }
}
