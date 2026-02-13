package org.example.employmentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.AuditDTO;
import org.example.employmentsystem.entity.CompanyProfile;
import org.example.employmentsystem.service.CompanyProfileService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器 - 审核企业、后续扩展审核职位等
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CompanyProfileService companyProfileService;

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
}
