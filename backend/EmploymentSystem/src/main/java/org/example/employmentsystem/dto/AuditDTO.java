package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 管理员审核请求参数（审核企业、审核职位通用）
 */
@Data
public class AuditDTO {

    /** 审核结果：1-通过 2-拒绝 */
    private Integer auditStatus;

    /** 审核备注（拒绝时填写原因） */
    private String auditRemark;
}
