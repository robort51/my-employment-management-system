package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 发布/编辑职位 DTO
 */
@Data
public class JobPositionDTO {

    private String title;

    /** 类别：技术/产品/设计/运营/市场/其他 */
    private String category;

    private Integer salaryMin;

    private Integer salaryMax;

    private String city;

    /** 学历要求 */
    private String educationReq;

    /** 经验要求 */
    private String experienceReq;

    /** 职位描述 */
    private String description;
}
