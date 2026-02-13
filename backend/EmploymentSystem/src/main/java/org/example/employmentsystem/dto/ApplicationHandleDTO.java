package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 企业处理求职申请 DTO（查看/筛选/拒绝/录用）
 */
@Data
public class ApplicationHandleDTO {

    /** 目标状态：1-已查看 2-通过筛选 3-已拒绝 5-已录用 */
    private Integer status;

    /** 企业备注 */
    private String remark;
}
