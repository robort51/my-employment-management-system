package org.example.employmentsystem.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.dto.JobPositionDTO;
import org.example.employmentsystem.dto.JobQueryDTO;
import org.example.employmentsystem.entity.JobPosition;
import org.example.employmentsystem.mapper.JobPositionMapper;
import org.example.employmentsystem.service.JobPositionService;
import org.springframework.stereotype.Service;

/**
 * 职位 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class JobPositionServiceImpl implements JobPositionService {

    private final JobPositionMapper jobPositionMapper;

    @Override
    public void publish(Long companyId, JobPositionDTO dto) {
        if (StrUtil.isBlank(dto.getTitle())) {
            throw new IllegalArgumentException("职位名称不能为空");
        }
        JobPosition job = new JobPosition();
        job.setCompanyId(companyId);
        copyDtoToEntity(dto, job);
        job.setStatus(0); // 待审核
        jobPositionMapper.insert(job);
    }

    @Override
    public void update(Long id, Long companyId, JobPositionDTO dto) {
        JobPosition job = jobPositionMapper.selectById(id);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("职位不存在或无权操作");
        }
        copyDtoToEntity(dto, job);
        job.setStatus(0); // 修改后重新审核
        jobPositionMapper.updateById(job);
    }

    @Override
    public void offline(Long id, Long companyId) {
        JobPosition job = jobPositionMapper.selectById(id);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("职位不存在或无权操作");
        }
        job.setStatus(2); // 已下架
        jobPositionMapper.updateById(job);
    }

    @Override
    public JobPosition getById(Long id) {
        return jobPositionMapper.selectById(id);
    }

    @Override
    public IPage<JobPosition> getCompanyJobs(Long companyId, int pageNum, int pageSize) {
        LambdaQueryWrapper<JobPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobPosition::getCompanyId, companyId);
        wrapper.orderByDesc(JobPosition::getCreateTime);
        return jobPositionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public IPage<JobPosition> search(JobQueryDTO query) {
        LambdaQueryWrapper<JobPosition> wrapper = new LambdaQueryWrapper<>();
        // 只搜索已上架的职位
        wrapper.eq(JobPosition::getStatus, 1);

        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(JobPosition::getTitle, query.getKeyword());
        }
        if (StrUtil.isNotBlank(query.getCategory())) {
            wrapper.eq(JobPosition::getCategory, query.getCategory());
        }
        if (StrUtil.isNotBlank(query.getCity())) {
            wrapper.eq(JobPosition::getCity, query.getCity());
        }
        if (StrUtil.isNotBlank(query.getEducationReq())) {
            wrapper.eq(JobPosition::getEducationReq, query.getEducationReq());
        }
        if (query.getSalaryMin() != null) {
            wrapper.ge(JobPosition::getSalaryMin, query.getSalaryMin());
        }
        if (query.getSalaryMax() != null) {
            wrapper.le(JobPosition::getSalaryMax, query.getSalaryMax());
        }

        wrapper.orderByDesc(JobPosition::getCreateTime);
        return jobPositionMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    @Override
    public IPage<JobPosition> getPage(int pageNum, int pageSize, Integer status) {
        LambdaQueryWrapper<JobPosition> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(JobPosition::getStatus, status);
        }
        wrapper.orderByDesc(JobPosition::getCreateTime);
        return jobPositionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void audit(Long id, Integer status, String remark) {
        JobPosition job = jobPositionMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("职位不存在");
        }
        job.setStatus(status); // 1=通过上架  3=拒绝
        jobPositionMapper.updateById(job);
    }

    private void copyDtoToEntity(JobPositionDTO dto, JobPosition job) {
        job.setTitle(dto.getTitle());
        job.setCategory(dto.getCategory());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setCity(dto.getCity());
        job.setEducationReq(dto.getEducationReq());
        job.setExperienceReq(dto.getExperienceReq());
        job.setDescription(dto.getDescription());
    }
}
