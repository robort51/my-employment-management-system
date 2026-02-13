package org.example.employmentsystem.dto;

import lombok.Data;

/**
 * 登录成功后返回的数据
 */
@Data
public class LoginVO {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 角色 */
    private String role;

    /** 头像 */
    private String avatar;

    /** JWT Token */
    private String token;
}
