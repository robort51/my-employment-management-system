package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 职位搜索/筛选查询 DTO
 */
@Data
public class JobQueryDTO {

    /** 关键词（模糊匹配标题） */
    private String keyword;

    /** 类别筛选 */
    private String category;

    /** 城市筛选 */
    private String city;

    /** 学历要求筛选 */
    private String educationReq;

    /** 最低薪资（筛选 salaryMin >= 此值） */
    private Integer salaryMin;

    /** 最高薪资（筛选 salaryMax <= 此值） */
    private Integer salaryMax;

    /** 页码，默认1 */
    private Integer pageNum = 1;

    /** 每页条数，默认10 */
    private Integer pageSize = 10;
}
