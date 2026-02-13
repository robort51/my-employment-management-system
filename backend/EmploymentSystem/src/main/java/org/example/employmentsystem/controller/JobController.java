package org.example.employmentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.JobPositionDTO;
import org.example.employmentsystem.dto.JobQueryDTO;
import org.example.employmentsystem.entity.CompanyProfile;
import org.example.employmentsystem.entity.JobPosition;
import org.example.employmentsystem.service.CompanyProfileService;
import org.example.employmentsystem.service.JobPositionService;
import org.springframework.web.bind.annotation.*;

/**
 * 职位控制器
 */
@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobPositionService jobPositionService;
    private final CompanyProfileService companyProfileService;

    /**
     * 获取当前企业的 companyProfile ID
     */
    private Long getCompanyId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CompanyProfile profile = companyProfileService.getByUserId(userId);
        if (profile == null) {
            throw new BusinessException("请先完善企业档案");
        }
        return profile.getId();
    }

    // ===================== 企业端接口 =====================

    /**
     * 企业发布职位
     * POST /api/job/publish
     */
    @PostMapping("/publish")
    public Result<?> publish(HttpServletRequest request,
                             @RequestBody JobPositionDTO dto) {
        Long companyId = getCompanyId(request);
        jobPositionService.publish(companyId, dto);
        return Result.success("职位发布成功，等待审核", null);
    }

    /**
     * 企业编辑职位
     * PUT /api/job/update/{id}
     */
    @PutMapping("/update/{id}")
    public Result<?> update(HttpServletRequest request,
                            @PathVariable Long id,
                            @RequestBody JobPositionDTO dto) {
        Long companyId = getCompanyId(request);
        jobPositionService.update(id, companyId, dto);
        return Result.success("职位已更新，等待重新审核", null);
    }

    /**
     * 企业下架职位
     * PUT /api/job/offline/{id}
     */
    @PutMapping("/offline/{id}")
    public Result<?> offline(HttpServletRequest request,
                             @PathVariable Long id) {
        Long companyId = getCompanyId(request);
        jobPositionService.offline(id, companyId);
        return Result.success("职位已下架", null);
    }

    /**
     * 企业查看自己发布的职位列表
     * GET /api/job/my?pageNum=1&pageSize=10
     */
    @GetMapping("/my")
    public Result<IPage<JobPosition>> myJobs(HttpServletRequest request,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        Long companyId = getCompanyId(request);
        IPage<JobPosition> page = jobPositionService.getCompanyJobs(companyId, pageNum, pageSize);
        return Result.success(page);
    }

    // ===================== 学生端 / 公开接口 =====================

    /**
     * 搜索已上架职位（分页 + 多条件筛选）
     * GET /api/job/search?keyword=Java&city=北京&pageNum=1&pageSize=10
     */
    @GetMapping("/search")
    public Result<IPage<JobPosition>> search(JobQueryDTO query) {
        IPage<JobPosition> page = jobPositionService.search(query);
        return Result.success(page);
    }

    /**
     * 获取职位详情
     * GET /api/job/{id}
     */
    @GetMapping("/{id}")
    public Result<JobPosition> detail(@PathVariable Long id) {
        JobPosition job = jobPositionService.getById(id);
        return Result.success(job);
    }
}
