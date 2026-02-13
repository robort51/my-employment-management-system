package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.entity.JobApplication;

public interface JobApplicationService {

    /** 学生投递简历 */
    void apply(Long studentId, Long jobId);

    /** 学生查看自己的投递记录（分页） */
    IPage<JobApplication> getStudentApplications(Long studentId, int pageNum, int pageSize);

    /** 企业查看某个职位收到的申请列表（分页） */
    IPage<JobApplication> getJobApplications(Long jobId, int pageNum, int pageSize);

    /** 企业处理申请（查看/筛选/拒绝/录用） */
    void handle(Long id, Long companyId, Integer status, String remark);

    /** 获取单条申请详情 */
    JobApplication getById(Long id);

    /** 企业查看收到的所有申请（通过企业下所有职位汇总） */
    IPage<JobApplication> getCompanyApplications(Long companyId, int pageNum, int pageSize);
}
