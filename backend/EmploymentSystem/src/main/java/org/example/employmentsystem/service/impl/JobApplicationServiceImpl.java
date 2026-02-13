package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.entity.JobApplication;
import org.example.employmentsystem.entity.JobPosition;
import org.example.employmentsystem.mapper.JobApplicationMapper;
import org.example.employmentsystem.mapper.JobPositionMapper;
import org.example.employmentsystem.service.JobApplicationService;
import org.springframework.stereotype.Service;

/**
 * 求职申请 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationMapper jobApplicationMapper;
    private final JobPositionMapper jobPositionMapper;

    @Override
    public void apply(Long studentId, Long jobId) {
        // 检查职位是否存在且已上架
        JobPosition job = jobPositionMapper.selectById(jobId);
        if (job == null || job.getStatus() != 1) {
            throw new BusinessException("职位不存在或未上架");
        }

        // 检查是否已经投递过
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobApplication::getStudentId, studentId)
               .eq(JobApplication::getJobId, jobId);
        if (jobApplicationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("您已投递过该职位，请勿重复投递");
        }

        JobApplication application = new JobApplication();
        application.setStudentId(studentId);
        application.setJobId(jobId);
        application.setStatus(0); // 已投递
        jobApplicationMapper.insert(application);
    }

    @Override
    public IPage<JobApplication> getStudentApplications(Long studentId, int pageNum, int pageSize) {
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobApplication::getStudentId, studentId)
               .orderByDesc(JobApplication::getApplyTime);
        return jobApplicationMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public IPage<JobApplication> getJobApplications(Long jobId, int pageNum, int pageSize) {
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobApplication::getJobId, jobId)
               .orderByDesc(JobApplication::getApplyTime);
        return jobApplicationMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void handle(Long id, Long companyId, Integer status, String remark) {
        JobApplication application = jobApplicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        // 验证该申请对应的职位属于当前企业
        JobPosition job = jobPositionMapper.selectById(application.getJobId());
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作此申请");
        }

        application.setStatus(status);
        application.setRemark(remark);
        jobApplicationMapper.updateById(application);
    }

    @Override
    public JobApplication getById(Long id) {
        return jobApplicationMapper.selectById(id);
    }
}
