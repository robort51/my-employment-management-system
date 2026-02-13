package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 企业档案 - 保存/更新请求参数
 */
@Data
public class CompanyProfileDTO {

    private String companyName;
    private String licenseUrl;
    private String industry;
    private String scale;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String description;
}
