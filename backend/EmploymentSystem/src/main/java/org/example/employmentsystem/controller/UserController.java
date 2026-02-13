package org.example.employmentsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.dto.LoginDTO;
import org.example.employmentsystem.dto.LoginVO;
import org.example.employmentsystem.dto.RegisterDTO;
import org.example.employmentsystem.entity.SysUser;
import org.example.employmentsystem.service.SysUserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 - 注册、登录、获取用户信息
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 用户注册
     * POST /api/user/register
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO registerDTO) {
        sysUserService.register(registerDTO);
        return Result.success("注册成功", null);
    }

    /**
     * 用户登录
     * POST /api/user/login
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = sysUserService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 获取当前登录用户信息
     * GET /api/user/info
     * 需要登录（携带 Token）
     */
    @GetMapping("/info")
    public Result<SysUser> getUserInfo(HttpServletRequest request) {
        // 从拦截器中设置的属性获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = sysUserService.getUserById(userId);
        return Result.success(user);
    }
}
