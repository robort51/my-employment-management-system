package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.dto.InterviewDTO;
import org.example.employmentsystem.entity.*;
import org.example.employmentsystem.mapper.InterviewMapper;
import org.example.employmentsystem.mapper.JobApplicationMapper;
import org.example.employmentsystem.mapper.JobPositionMapper;
import org.example.employmentsystem.mapper.StudentProfileMapper;
import org.example.employmentsystem.mapper.CompanyProfileMapper;
import org.example.employmentsystem.service.InterviewService;
import org.example.employmentsystem.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 面试邀约 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewMapper interviewMapper;
    private final JobApplicationMapper jobApplicationMapper;
    private final JobPositionMapper jobPositionMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final CompanyProfileMapper companyProfileMapper;
    private final NotificationService notificationService;

    @Override
    public void invite(Long companyId, InterviewDTO dto) {
        // 校验申请存在，且对应职位属于当前企业
        JobApplication application = jobApplicationMapper.selectById(dto.getApplicationId());
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        JobPosition job = jobPositionMapper.selectById(application.getJobId());
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作此申请");
        }

        // 检查是否已发送过面试邀请
        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interview::getApplicationId, dto.getApplicationId());
        if (interviewMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已发送过面试邀请，请勿重复发送");
        }

        // 创建面试记录
        Interview interview = new Interview();
        interview.setApplicationId(dto.getApplicationId());
        interview.setInterviewTime(dto.getInterviewTime());
        interview.setLocation(dto.getLocation());
        interview.setType(dto.getType());
        interview.setMeetingLink(dto.getMeetingLink());
        interview.setRemark(dto.getRemark());
        interview.setStatus(0); // 待确认
        interviewMapper.insert(interview);

        // 更新申请状态为"已邀面试"
        application.setStatus(4);
        jobApplicationMapper.updateById(application);
        // 通知学生收到面试邀请
        StudentProfile student = studentProfileMapper.selectById(application.getStudentId());
        if (student != null) {
            notificationService.send(student.getUserId(),
                    "收到面试邀请",
                    "您投递的职位【" + job.getTitle() + "】已收到面试邀请，请及时确认",
                    "interview");
        }    }

    @Override
    public void respond(Long id, Long studentId, Integer status) {
        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            throw new BusinessException("面试记录不存在");
        }

        // 验证该面试属于当前学生
        JobApplication application = jobApplicationMapper.selectById(interview.getApplicationId());
        if (application == null || !application.getStudentId().equals(studentId)) {
            throw new BusinessException("无权操作此面试");
        }

        if (interview.getStatus() != 0) {
            throw new BusinessException("该面试邀请已处理");
        }

        // status: 1=接受  2=拒绝
        interview.setStatus(status);
        interviewMapper.updateById(interview);

        // 通知企业学生的回应
        JobPosition job = jobPositionMapper.selectById(application.getJobId());
        CompanyProfile company = (job != null) ? companyProfileMapper.selectById(job.getCompanyId()) : null;
        if (company != null) {
            String action = (status == 1) ? "接受" : "拒绝";
            notificationService.send(company.getUserId(),
                    "面试邀请已" + action,
                    "应聘者已" + action + "您发出的【" + job.getTitle() + "】面试邀请",
                    "interview");
        }
    }

    @Override
    public void fillResult(Long id, Long companyId, Integer result, String feedback) {
        Interview interview = getAndCheckCompanyInterview(id, companyId);

        interview.setResult(result);
        interview.setFeedback(feedback);
        interview.setStatus(3); // 标记为已完成
        interviewMapper.updateById(interview);

        // 如果面试通过，可同时更新申请状态为"已录用"
        if (result == 1) {
            JobApplication application = jobApplicationMapper.selectById(interview.getApplicationId());
            if (application != null) {
                application.setStatus(5); // 已录用
                jobApplicationMapper.updateById(application);
            }
        }
        // 通知学生面试结果
        JobApplication app = jobApplicationMapper.selectById(interview.getApplicationId());
        if (app != null) {
            StudentProfile student = studentProfileMapper.selectById(app.getStudentId());
            JobPosition job = jobPositionMapper.selectById(app.getJobId());
            if (student != null && job != null) {
                String resultText = (result == 1) ? "通过" : "未通过";
                notificationService.send(student.getUserId(),
                        "面试结果通知",
                        "您投递的职位【" + job.getTitle() + "】面试结果：" + resultText,
                        "interview");
            }
        }    }

    @Override
    public void complete(Long id, Long companyId) {
        Interview interview = getAndCheckCompanyInterview(id, companyId);
        interview.setStatus(3); // 已完成
        interviewMapper.updateById(interview);
    }

    @Override
    public void cancel(Long id, Long companyId) {
        Interview interview = getAndCheckCompanyInterview(id, companyId);
        interview.setStatus(4); // 已取消
        interviewMapper.updateById(interview);
    }

    @Override
    public Interview getByApplicationId(Long applicationId) {
        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interview::getApplicationId, applicationId);
        return interviewMapper.selectOne(wrapper);
    }

    @Override
    public IPage<Interview> getStudentInterviews(Long studentId, int pageNum, int pageSize) {
        // 先查出学生的所有申请ID
        LambdaQueryWrapper<JobApplication> appWrapper = new LambdaQueryWrapper<>();
        appWrapper.eq(JobApplication::getStudentId, studentId)
                  .select(JobApplication::getId);
        List<Long> appIds = jobApplicationMapper.selectList(appWrapper)
                .stream().map(JobApplication::getId).collect(Collectors.toList());

        if (appIds.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Interview::getApplicationId, appIds)
               .orderByDesc(Interview::getCreateTime);
        return interviewMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public IPage<Interview> getCompanyInterviews(Long companyId, int pageNum, int pageSize) {
        // 先查出企业的所有职位ID
        LambdaQueryWrapper<JobPosition> jobWrapper = new LambdaQueryWrapper<>();
        jobWrapper.eq(JobPosition::getCompanyId, companyId)
                  .select(JobPosition::getId);
        List<Long> jobIds = jobPositionMapper.selectList(jobWrapper)
                .stream().map(JobPosition::getId).collect(Collectors.toList());

        if (jobIds.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 再查出这些职位对应的申请ID
        LambdaQueryWrapper<JobApplication> appWrapper = new LambdaQueryWrapper<>();
        appWrapper.in(JobApplication::getJobId, jobIds)
                  .select(JobApplication::getId);
        List<Long> appIds = jobApplicationMapper.selectList(appWrapper)
                .stream().map(JobApplication::getId).collect(Collectors.toList());

        if (appIds.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Interview::getApplicationId, appIds)
               .orderByDesc(Interview::getCreateTime);
        return interviewMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 校验面试记录存在且属于当前企业
     */
    private Interview getAndCheckCompanyInterview(Long interviewId, Long companyId) {
        Interview interview = interviewMapper.selectById(interviewId);
        if (interview == null) {
            throw new BusinessException("面试记录不存在");
        }
        JobApplication application = jobApplicationMapper.selectById(interview.getApplicationId());
        if (application == null) {
            throw new BusinessException("关联申请不存在");
        }
        JobPosition job = jobPositionMapper.selectById(application.getJobId());
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作此面试");
        }
        return interview;
    }
}
