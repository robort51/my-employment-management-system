package org.example.employmentsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业档案实体类 - 对应 company_profile 表
 */
@Data
@TableName("company_profile")
public class CompanyProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 企业名称 */
    private String companyName;

    /** 营业执照图片URL */
    private String licenseUrl;

    /** 所属行业 */
    private String industry;

    /** 企业规模 */
    private String scale;

    /** 企业地址 */
    private String address;

    /** 联系人 */
    private String contactPerson;

    /** 联系电话 */
    private String contactPhone;

    /** 企业简介 */
    private String description;

    /** 审核状态：0-待审核 1-已通过 2-已拒绝 */
    private Integer auditStatus;

    /** 审核备注 */
    private String auditRemark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
