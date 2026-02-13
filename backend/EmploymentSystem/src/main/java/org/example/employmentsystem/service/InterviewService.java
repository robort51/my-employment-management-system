package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.dto.InterviewDTO;
import org.example.employmentsystem.entity.Interview;

public interface InterviewService {

    /** 企业发起面试邀请（同时更新申请状态为"已邀面试"） */
    void invite(Long companyId, InterviewDTO dto);

    /** 学生接受/拒绝面试邀请 */
    void respond(Long id, Long studentId, Integer status);

    /** 企业填写面试结果 */
    void fillResult(Long id, Long companyId, Integer result, String feedback);

    /** 企业标记面试完成 */
    void complete(Long id, Long companyId);

    /** 企业取消面试 */
    void cancel(Long id, Long companyId);

    /** 根据申请ID查面试记录 */
    Interview getByApplicationId(Long applicationId);

    /** 学生查看自己的面试列表 */
    IPage<Interview> getStudentInterviews(Long studentId, int pageNum, int pageSize);

    /** 企业查看自己发出的面试列表 */
    IPage<Interview> getCompanyInterviews(Long companyId, int pageNum, int pageSize);
}
