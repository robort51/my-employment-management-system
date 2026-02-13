package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 注册请求参数
 */
@Data
public class RegisterDTO {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 角色：student / company */
    private String role;
}
