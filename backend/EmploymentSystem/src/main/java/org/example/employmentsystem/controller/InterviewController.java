package org.example.employmentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.InterviewDTO;
import org.example.employmentsystem.dto.InterviewResultDTO;
import org.example.employmentsystem.entity.CompanyProfile;
import org.example.employmentsystem.entity.Interview;
import org.example.employmentsystem.entity.StudentProfile;
import org.example.employmentsystem.service.CompanyProfileService;
import org.example.employmentsystem.service.InterviewService;
import org.example.employmentsystem.service.StudentProfileService;
import org.springframework.web.bind.annotation.*;

/**
 * 面试邀约控制器
 */
@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
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

    // ===================== 企业端 =====================

    /**
     * 企业发起面试邀请
     * POST /api/interview/invite
     */
    @PostMapping("/invite")
    public Result<?> invite(HttpServletRequest request,
                            @RequestBody InterviewDTO dto) {
        Long companyId = getCompanyId(request);
        interviewService.invite(companyId, dto);
        return Result.success("面试邀请已发送", null);
    }

    /**
     * 企业填写面试结果
     * PUT /api/interview/result/{id}
     */
    @PutMapping("/result/{id}")
    public Result<?> fillResult(HttpServletRequest request,
                                @PathVariable Long id,
                                @RequestBody InterviewResultDTO dto) {
        Long companyId = getCompanyId(request);
        interviewService.fillResult(id, companyId, dto.getResult(), dto.getFeedback());
        return Result.success("面试结果已记录", null);
    }

    /**
     * 企业取消面试
     * PUT /api/interview/cancel/{id}
     */
    @PutMapping("/cancel/{id}")
    public Result<?> cancel(HttpServletRequest request,
                            @PathVariable Long id) {
        Long companyId = getCompanyId(request);
        interviewService.cancel(id, companyId);
        return Result.success("面试已取消", null);
    }

    /**
     * 企业查看自己发出的面试列表
     * GET /api/interview/company?pageNum=1&pageSize=10
     */
    @GetMapping("/company")
    public Result<IPage<Interview>> companyInterviews(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long companyId = getCompanyId(request);
        IPage<Interview> page = interviewService.getCompanyInterviews(companyId, pageNum, pageSize);
        return Result.success(page);
    }

    // ===================== 学生端 =====================

    /**
     * 学生接受面试邀请
     * PUT /api/interview/accept/{id}
     */
    @PutMapping("/accept/{id}")
    public Result<?> accept(HttpServletRequest request,
                            @PathVariable Long id) {
        Long studentId = getStudentId(request);
        interviewService.respond(id, studentId, 1); // 1=接受
        return Result.success("已接受面试邀请", null);
    }

    /**
     * 学生拒绝面试邀请
     * PUT /api/interview/reject/{id}
     */
    @PutMapping("/reject/{id}")
    public Result<?> reject(HttpServletRequest request,
                            @PathVariable Long id) {
        Long studentId = getStudentId(request);
        interviewService.respond(id, studentId, 2); // 2=拒绝
        return Result.success("已拒绝面试邀请", null);
    }

    /**
     * 学生查看自己的面试列表
     * GET /api/interview/my?pageNum=1&pageSize=10
     */
    @GetMapping("/my")
    public Result<IPage<Interview>> myInterviews(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long studentId = getStudentId(request);
        IPage<Interview> page = interviewService.getStudentInterviews(studentId, pageNum, pageSize);
        return Result.success(page);
    }

    // ===================== 公共 =====================

    /**
     * 根据申请ID查看面试详情
     * GET /api/interview/application/{applicationId}
     */
    @GetMapping("/application/{applicationId}")
    public Result<Interview> getByApplication(@PathVariable Long applicationId) {
        Interview interview = interviewService.getByApplicationId(applicationId);
        return Result.success(interview);
    }
}
