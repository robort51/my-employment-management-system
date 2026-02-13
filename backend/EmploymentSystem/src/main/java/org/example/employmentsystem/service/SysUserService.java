package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.dto.LoginDTO;
import org.example.employmentsystem.dto.LoginVO;
import org.example.employmentsystem.dto.RegisterDTO;
import org.example.employmentsystem.entity.SysUser;

/**
 * 用户 Service 接口
 */
public interface SysUserService {

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据ID获取用户信息
     */
    SysUser getUserById(Long id);

    /**
     * 分页查询用户列表
     */
    IPage<SysUser> getUserPage(int pageNum, int pageSize);

    /**
     * 切换用户状态（启用/禁用）
     */
    void toggleStatus(Long id);
}
