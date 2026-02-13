package org.example.employmentsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.employmentsystem.entity.SysUser;

/**
 * 用户 Mapper 接口
 * 继承 BaseMapper 自动拥有 CRUD 方法，无需手写 SQL
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
