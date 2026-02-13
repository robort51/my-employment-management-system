package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.dto.CompanyProfileDTO;
import org.example.employmentsystem.entity.CompanyProfile;

/**
 * 企业档案 Service 接口
 */
public interface CompanyProfileService {

    /**
     * 获取当前企业的档案
     */
    CompanyProfile getByUserId(Long userId);

    /**
     * 根据企业档案ID获取
     */
    CompanyProfile getById(Long id);

    /**
     * 保存或更新企业档案
     */
    void saveOrUpdate(Long userId, CompanyProfileDTO dto);

    /**
     * 分页查询企业列表（管理员用，可按审核状态筛选）
     */
    IPage<CompanyProfile> getPage(int pageNum, int pageSize, Integer auditStatus);

    /**
     * 审核企业（管理员操作）
     */
    void audit(Long id, Integer auditStatus, String auditRemark);
}
