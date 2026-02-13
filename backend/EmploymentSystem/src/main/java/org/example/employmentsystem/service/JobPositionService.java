package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.dto.JobPositionDTO;
import org.example.employmentsystem.dto.JobQueryDTO;
import org.example.employmentsystem.entity.JobPosition;

public interface JobPositionService {

    /** 企业发布职位 */
    void publish(Long companyId, JobPositionDTO dto);

    /** 企业编辑职位（重新进入待审核状态） */
    void update(Long id, Long companyId, JobPositionDTO dto);

    /** 企业下架职位 */
    void offline(Long id, Long companyId);

    /** 获取单个职位详情 */
    JobPosition getById(Long id);

    /** 企业查看自己发布的职位列表（分页） */
    IPage<JobPosition> getCompanyJobs(Long companyId, int pageNum, int pageSize);

    /** 学生搜索已上架职位（分页 + 筛选） */
    IPage<JobPosition> search(JobQueryDTO query);

    /** 管理员分页查询职位（可按状态筛选） */
    IPage<JobPosition> getPage(int pageNum, int pageSize, Integer status);

    /** 管理员审核职位 */
    void audit(Long id, Integer status, String remark);
}
