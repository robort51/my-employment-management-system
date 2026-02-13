package org.example.employmentsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.employmentsystem.entity.JobPosition;

@Mapper
public interface JobPositionMapper extends BaseMapper<JobPosition> {
}
