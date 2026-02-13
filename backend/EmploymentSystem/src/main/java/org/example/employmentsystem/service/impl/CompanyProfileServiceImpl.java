package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.dto.CompanyProfileDTO;
import org.example.employmentsystem.entity.CompanyProfile;
import org.example.employmentsystem.mapper.CompanyProfileMapper;
import org.example.employmentsystem.service.CompanyProfileService;
import org.example.employmentsystem.service.NotificationService;
import org.springframework.stereotype.Service;

/**
 * 企业档案 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private final CompanyProfileMapper companyProfileMapper;
    private final NotificationService notificationService;

    @Override
    public CompanyProfile getByUserId(Long userId) {
        LambdaQueryWrapper<CompanyProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompanyProfile::getUserId, userId);
        return companyProfileMapper.selectOne(wrapper);
    }

    @Override
    public CompanyProfile getById(Long id) {
        return companyProfileMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(Long userId, CompanyProfileDTO dto) {
        CompanyProfile profile = getByUserId(userId);

        if (profile == null) {
            // 新建
            profile = new CompanyProfile();
            profile.setUserId(userId);
            copyDtoToEntity(dto, profile);
            profile.setAuditStatus(0); // 新建默认待审核
            companyProfileMapper.insert(profile);
        } else {
            // 更新后重新设为待审核
            copyDtoToEntity(dto, profile);
            profile.setAuditStatus(0);
            profile.setAuditRemark(null);
            companyProfileMapper.updateById(profile);
        }
    }

    @Override
    public IPage<CompanyProfile> getPage(int pageNum, int pageSize, Integer auditStatus) {
        LambdaQueryWrapper<CompanyProfile> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) {
            wrapper.eq(CompanyProfile::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(CompanyProfile::getCreateTime);
        return companyProfileMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void audit(Long id, Integer auditStatus, String auditRemark) {
        CompanyProfile profile = companyProfileMapper.selectById(id);
        if (profile == null) {
            throw new BusinessException("企业不存在");
        }
        profile.setAuditStatus(auditStatus);
        profile.setAuditRemark(auditRemark);
        companyProfileMapper.updateById(profile);

        // 通知企业审核结果
        String statusText = (auditStatus == 1) ? "已通过" : "未通过";
        String remarkPart = (auditRemark != null && !auditRemark.isBlank()) ? "，备注：" + auditRemark : "";
        notificationService.send(profile.getUserId(),
                "企业认证审核结果",
                "您的企业认证审核" + statusText + remarkPart,
                "audit");
    }

    private void copyDtoToEntity(CompanyProfileDTO dto, CompanyProfile profile) {
        profile.setCompanyName(dto.getCompanyName());
        profile.setLicenseUrl(dto.getLicenseUrl());
        profile.setIndustry(dto.getIndustry());
        profile.setScale(dto.getScale());
        profile.setAddress(dto.getAddress());
        profile.setContactPerson(dto.getContactPerson());
        profile.setContactPhone(dto.getContactPhone());
        profile.setDescription(dto.getDescription());
    }
}
