package org.example.employmentsystem.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.dto.LoginDTO;
import org.example.employmentsystem.dto.LoginVO;
import org.example.employmentsystem.dto.RegisterDTO;
import org.example.employmentsystem.entity.SysUser;
import org.example.employmentsystem.mapper.SysUserMapper;
import org.example.employmentsystem.service.SysUserService;
import org.example.employmentsystem.utils.JwtUtils;
import org.springframework.stereotype.Service;

/**
 * 用户 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public void register(RegisterDTO registerDTO) {
        // 1. 参数校验
        if (registerDTO.getUsername() == null || registerDTO.getUsername().isBlank()) {
            throw new BusinessException("用户名不能为空");
        }
        if (registerDTO.getPassword() == null || registerDTO.getPassword().isBlank()) {
            throw new BusinessException("密码不能为空");
        }
        if (registerDTO.getRole() == null || registerDTO.getRole().isBlank()) {
            throw new BusinessException("角色不能为空");
        }
        // 只允许注册学生和企业，管理员不开放注册
        if (!"student".equals(registerDTO.getRole()) && !"company".equals(registerDTO.getRole())) {
            throw new BusinessException("角色类型不合法");
        }

        // 2. 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, registerDTO.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 创建用户
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(DigestUtil.md5Hex(registerDTO.getPassword())); // MD5加密
        user.setRole(registerDTO.getRole());
        user.setStatus(1); // 注册后默认正常状态

        sysUserMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 参数校验
        if (loginDTO.getUsername() == null || loginDTO.getUsername().isBlank()) {
            throw new BusinessException("用户名不能为空");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().isBlank()) {
            throw new BusinessException("密码不能为空");
        }

        // 2. 查找用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户名不存在");
        }

        // 3. 校验密码
        String inputPassword = DigestUtil.md5Hex(loginDTO.getPassword());
        if (!inputPassword.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 4. 校验状态
        if (user.getStatus() == 2) {
            throw new BusinessException("账号已被禁用");
        }

        // 5. 生成 Token 并返回
        String token = JwtUtils.generateToken(user.getId(), user.getRole());

        LoginVO loginVO = new LoginVO();
        loginVO.setId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRole(user.getRole());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setToken(token);

        return loginVO;
    }

    @Override
    public SysUser getUserById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return user;
    }

    @Override
    public IPage<SysUser> getUserPage(int pageNum, int pageSize) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> page = sysUserMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        // 清除密码字段
        page.getRecords().forEach(u -> u.setPassword(null));
        return page;
    }

    @Override
    public void toggleStatus(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("admin".equals(user.getRole())) {
            throw new BusinessException("不能禁用管理员账号");
        }
        user.setStatus(user.getStatus() == 1 ? 2 : 1);
        sysUserMapper.updateById(user);
    }
}
