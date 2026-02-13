package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 招聘职位实体类 - 对应 job_position 表
 */
@Data
@TableName("job_position")
public class JobPosition {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联企业档案ID */
    private Long companyId;

    /** 职位名称 */
    private String title;

    /** 职位类别：技术/产品/设计/运营/市场/其他 */
    private String category;

    /** 最低薪资（元/月） */
    private Integer salaryMin;

    /** 最高薪资（元/月） */
    private Integer salaryMax;

    /** 工作城市 */
    private String city;

    /** 学历要求 */
    private String educationReq;

    /** 经验要求 */
    private String experienceReq;

    /** 职位描述 */
    private String description;

    /** 状态：0-待审核 1-已上架 2-已下架 3-审核拒绝 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
