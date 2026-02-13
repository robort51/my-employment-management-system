package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.entity.CompanyProfile;
import org.example.employmentsystem.entity.JobApplication;
import org.example.employmentsystem.entity.JobPosition;
import org.example.employmentsystem.entity.StudentProfile;
import org.example.employmentsystem.mapper.CompanyProfileMapper;
import org.example.employmentsystem.mapper.JobApplicationMapper;
import org.example.employmentsystem.mapper.JobPositionMapper;
import org.example.employmentsystem.mapper.StudentProfileMapper;
import org.example.employmentsystem.service.JobApplicationService;
import org.example.employmentsystem.service.NotificationService;
import org.springframework.stereotype.Service;

/**
 * 求职申请 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationMapper jobApplicationMapper;
    private final JobPositionMapper jobPositionMapper;
    private final CompanyProfileMapper companyProfileMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final NotificationService notificationService;

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

        // 通知企业有新的简历投递
        CompanyProfile company = companyProfileMapper.selectById(job.getCompanyId());
        if (company != null) {
            notificationService.send(company.getUserId(),
                    "收到新的简历投递",
                    "您发布的职位【" + job.getTitle() + "】收到了新的简历投递",
                    "apply");
        }
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
    public IPage<JobApplication> getCompanyApplications(Long companyId, int pageNum, int pageSize) {
        // 查询企业下所有职位ID
        LambdaQueryWrapper<JobPosition> jobWrapper = new LambdaQueryWrapper<>();
        jobWrapper.eq(JobPosition::getCompanyId, companyId)
                  .select(JobPosition::getId);
        java.util.List<Long> jobIds = jobPositionMapper.selectList(jobWrapper)
                .stream().map(JobPosition::getId).toList();
        if (jobIds.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(JobApplication::getJobId, jobIds)
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

        // 通知学生申请处理结果
        StudentProfile student = studentProfileMapper.selectById(application.getStudentId());
        if (student != null) {
            String statusText = switch (status) {
                case 1 -> "已查看";
                case 2 -> "通过筛选";
                case 3 -> "未通过";
                case 5 -> "已录用";
                default -> "已更新";
            };
            notificationService.send(student.getUserId(),
                    "求职申请状态更新",
                    "您投递的职位【" + job.getTitle() + "】状态已更新为：" + statusText,
                    "apply");
        }
    }

    @Override
    public JobApplication getById(Long id) {
        return jobApplicationMapper.selectById(id);
    }
}
