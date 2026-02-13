package org.example.employmentsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.employmentsystem.entity.JobApplication;

@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {
}
