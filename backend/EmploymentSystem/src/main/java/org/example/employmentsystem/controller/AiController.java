package org.example.employmentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.AiMockAnswerDTO;
import org.example.employmentsystem.dto.AiMockInterviewDTO;
import org.example.employmentsystem.dto.AiResumePolishDTO;
import org.example.employmentsystem.entity.*;
import org.example.employmentsystem.service.*;
import org.springframework.web.bind.annotation.*;

/**
 * AI 功能控制器（简历润色 / 模拟面试 / 职业规划）
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiResumeService aiResumeService;
    private final AiInterviewService aiInterviewService;
    private final CareerPlanService careerPlanService;
    private final StudentProfileService studentProfileService;

    /** 获取学生档案ID */
    private Long getStudentId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        StudentProfile profile = studentProfileService.getByUserId(userId);
        if (profile == null) {
            throw new BusinessException("请先完善学生档案");
        }
        return profile.getId();
    }

    /** 获取学生档案（用于职业规划） */
    private StudentProfile getStudentProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        StudentProfile profile = studentProfileService.getByUserId(userId);
        if (profile == null) {
            throw new BusinessException("请先完善学生档案");
        }
        return profile;
    }

    // ===================== 简历润色 =====================

    /**
     * AI 简历润色
     * POST /api/ai/resume/polish
     */
    @PostMapping("/resume/polish")
    public Result<AiResumeRecord> polishResume(HttpServletRequest request,
                                               @RequestBody AiResumePolishDTO dto) {
        Long studentId = getStudentId(request);
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new BusinessException("简历内容不能为空");
        }
        AiResumeRecord record = aiResumeService.polish(studentId, dto.getContent());
        return Result.success(record);
    }

    /**
     * 查看简历润色历史记录
     * GET /api/ai/resume/records?pageNum=1&pageSize=10
     */
    @GetMapping("/resume/records")
    public Result<IPage<AiResumeRecord>> resumeRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long studentId = getStudentId(request);
        return Result.success(aiResumeService.getRecords(studentId, pageNum, pageSize));
    }

    // ===================== 模拟面试 =====================

    /**
     * 生成模拟面试题目
     * POST /api/ai/interview/start
     */
    @PostMapping("/interview/start")
    public Result<AiInterviewRecord> startInterview(HttpServletRequest request,
                                                     @RequestBody AiMockInterviewDTO dto) {
        Long studentId = getStudentId(request);
        if (dto.getJobTitle() == null || dto.getJobTitle().isBlank()) {
            throw new BusinessException("目标职位不能为空");
        }
        AiInterviewRecord record = aiInterviewService.generateQuestions(studentId, dto.getJobTitle());
        return Result.success(record);
    }

    /**
     * 提交模拟面试答案，获取AI评价
     * POST /api/ai/interview/submit
     */
    @PostMapping("/interview/submit")
    public Result<AiInterviewRecord> submitAnswers(HttpServletRequest request,
                                                    @RequestBody AiMockAnswerDTO dto) {
        Long studentId = getStudentId(request);
        AiInterviewRecord record = aiInterviewService.submitAnswers(dto.getRecordId(), studentId, dto.getAnswers());
        return Result.success(record);
    }

    /**
     * 查看模拟面试历史记录
     * GET /api/ai/interview/records?pageNum=1&pageSize=10
     */
    @GetMapping("/interview/records")
    public Result<IPage<AiInterviewRecord>> interviewRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long studentId = getStudentId(request);
        return Result.success(aiInterviewService.getRecords(studentId, pageNum, pageSize));
    }

    /**
     * 查看单条模拟面试详情
     * GET /api/ai/interview/{id}
     */
    @GetMapping("/interview/{id}")
    public Result<AiInterviewRecord> interviewDetail(@PathVariable Long id) {
        return Result.success(aiInterviewService.getById(id));
    }

    // ===================== 职业规划 =====================

    /**
     * AI 生成职业规划（自动读取学生档案信息）
     * POST /api/ai/career/generate
     */
    @PostMapping("/career/generate")
    public Result<CareerPlan> generateCareerPlan(HttpServletRequest request) {
        StudentProfile profile = getStudentProfile(request);

        // 自动拼接学生信息
        StringBuilder info = new StringBuilder();
        info.append("姓名：").append(profile.getRealName()).append("\n");
        info.append("大学：").append(profile.getUniversity()).append("\n");
        info.append("专业：").append(profile.getMajor()).append("\n");
        info.append("学历：").append(profile.getEducation()).append("\n");
        info.append("毕业年份：").append(profile.getGraduationYear()).append("\n");
        if (profile.getSkills() != null) {
            info.append("技能：").append(profile.getSkills()).append("\n");
        }
        if (profile.getSelfIntroduction() != null) {
            info.append("自我介绍：").append(profile.getSelfIntroduction()).append("\n");
        }

        CareerPlan plan = careerPlanService.generate(profile.getId(), info.toString());
        return Result.success(plan);
    }

    /**
     * 查看职业规划历史记录
     * GET /api/ai/career/records?pageNum=1&pageSize=10
     */
    @GetMapping("/career/records")
    public Result<IPage<CareerPlan>> careerRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long studentId = getStudentId(request);
        return Result.success(careerPlanService.getRecords(studentId, pageNum, pageSize));
    }
}
