package org.example.employmentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.AuditDTO;
import org.example.employmentsystem.entity.CompanyProfile;
import org.example.employmentsystem.entity.JobPosition;
import org.example.employmentsystem.entity.SysUser;
import org.example.employmentsystem.service.CompanyProfileService;
import org.example.employmentsystem.service.JobPositionService;
import org.example.employmentsystem.service.SysUserService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器 - 审核企业、审核职位、用户管理
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CompanyProfileService companyProfileService;
    private final JobPositionService jobPositionService;
    private final SysUserService sysUserService;

    /**
     * 检查当前用户是否为管理员
     */
    private void checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            throw new BusinessException(403, "无权限，仅管理员可操作");
        }
    }

    /**
     * 分页查询企业列表（可按审核状态筛选）
     * GET /api/admin/company/list?pageNum=1&pageSize=10&auditStatus=0
     */
    @GetMapping("/company/list")
    public Result<IPage<CompanyProfile>> getCompanyList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer auditStatus) {
        checkAdmin(request);
        IPage<CompanyProfile> page = companyProfileService.getPage(pageNum, pageSize, auditStatus);
        return Result.success(page);
    }

    /**
     * 审核企业
     * PUT /api/admin/company/audit/{id}
     */
    @PutMapping("/company/audit/{id}")
    public Result<?> auditCompany(HttpServletRequest request,
                                  @PathVariable Long id,
                                  @RequestBody AuditDTO auditDTO) {
        checkAdmin(request);
        companyProfileService.audit(id, auditDTO.getAuditStatus(), auditDTO.getAuditRemark());
        return Result.success("审核完成", null);
    }

    // ===================== 职位审核 =====================

    /**
     * 分页查询职位列表（可按状态筛选）
     * GET /api/admin/job/list?pageNum=1&pageSize=10&status=0
     */
    @GetMapping("/job/list")
    public Result<IPage<JobPosition>> getJobList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        checkAdmin(request);
        IPage<JobPosition> page = jobPositionService.getPage(pageNum, pageSize, status);
        return Result.success(page);
    }

    /**
     * 审核职位：status=1 通过上架，status=3 拒绝
     * PUT /api/admin/job/audit/{id}
     */
    @PutMapping("/job/audit/{id}")
    public Result<?> auditJob(HttpServletRequest request,
                              @PathVariable Long id,
                              @RequestBody AuditDTO auditDTO) {
        checkAdmin(request);
        jobPositionService.audit(id, auditDTO.getAuditStatus(), auditDTO.getAuditRemark());
        return Result.success("职位审核完成", null);
    }

    // ===================== 用户管理 =====================

    /**
     * 分页查询用户列表
     * GET /api/admin/user/list?pageNum=1&pageSize=10
     */
    @GetMapping("/user/list")
    public Result<IPage<SysUser>> getUserList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        checkAdmin(request);
        IPage<SysUser> page = sysUserService.getUserPage(pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 切换用户状态（启用/禁用）
     * PUT /api/admin/user/toggle-status/{id}
     */
    @PutMapping("/user/toggle-status/{id}")
    public Result<?> toggleUserStatus(HttpServletRequest request,
                                       @PathVariable Long id) {
        checkAdmin(request);
        sysUserService.toggleStatus(id);
        return Result.success("操作成功", null);
    }
}
