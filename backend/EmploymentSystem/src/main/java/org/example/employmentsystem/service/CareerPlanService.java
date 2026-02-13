package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.entity.CareerPlan;

public interface CareerPlanService {

    /** AI生成职业规划 */
    CareerPlan generate(Long studentId, String studentInfo);

    /** 查看职业规划记录列表 */
    IPage<CareerPlan> getRecords(Long studentId, int pageNum, int pageSize);
}
