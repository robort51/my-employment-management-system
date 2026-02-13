package org.example.employmentsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.CompanyProfileDTO;
import org.example.employmentsystem.entity.CompanyProfile;
import org.example.employmentsystem.service.CompanyProfileService;
import org.springframework.web.bind.annotation.*;

/**
 * 企业档案控制器
 */
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyProfileService companyProfileService;

    /**
     * 获取当前企业的档案
     * GET /api/company/profile
     */
    @GetMapping("/profile")
    public Result<CompanyProfile> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CompanyProfile profile = companyProfileService.getByUserId(userId);
        return Result.success(profile);
    }

    /**
     * 保存或更新企业档案（提交后需管理员重新审核）
     * POST /api/company/profile
     */
    @PostMapping("/profile")
    public Result<?> saveProfile(HttpServletRequest request,
                                 @RequestBody CompanyProfileDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        companyProfileService.saveOrUpdate(userId, dto);
        return Result.success("企业信息已提交，等待管理员审核", null);
    }

    /**
     * 根据ID获取企业详情（公开接口，学生查看企业信息用）
     * GET /api/company/{id}
     */
    @GetMapping("/{id}")
    public Result<CompanyProfile> getById(@PathVariable Long id) {
        CompanyProfile profile = companyProfileService.getById(id);
        return Result.success(profile);
    }
}
