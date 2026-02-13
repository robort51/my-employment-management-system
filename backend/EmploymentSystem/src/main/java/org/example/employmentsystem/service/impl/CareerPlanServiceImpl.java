package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.entity.CareerPlan;
import org.example.employmentsystem.mapper.CareerPlanMapper;
import org.example.employmentsystem.service.AiService;
import org.example.employmentsystem.service.CareerPlanService;
import org.springframework.stereotype.Service;

/**
 * 职业规划 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class CareerPlanServiceImpl implements CareerPlanService {

    private final CareerPlanMapper careerPlanMapper;
    private final AiService aiService;

    private static final String SYSTEM_PROMPT = """
            你是一名专业的职业规划师，专门为大学生提供职业发展建议。
            请根据学生提供的个人信息（专业、技能、兴趣等），生成一份详细的职业规划方案。
            要求：
            1. 分析学生的优势和劣势
            2. 推荐2-3个适合的职业方向
            3. 为每个方向制定短期（1年）和中期（3年）发展路径
            4. 提供具体的学习和提升建议
            5. 推荐相关的学习资源或证书
            6. 语言积极正面，鼓励性强
            """;

    @Override
    public CareerPlan generate(Long studentId, String studentInfo) {
        // 调用 AI 生成职业规划
        String planContent = aiService.chat(SYSTEM_PROMPT,
                "以下是我的个人信息，请为我制定职业规划：\n\n" + studentInfo);

        // 保存记录
        CareerPlan plan = new CareerPlan();
        plan.setStudentId(studentId);
        plan.setPlanContent(planContent);
        careerPlanMapper.insert(plan);

        return plan;
    }

    @Override
    public IPage<CareerPlan> getRecords(Long studentId, int pageNum, int pageSize) {
        LambdaQueryWrapper<CareerPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareerPlan::getStudentId, studentId)
               .orderByDesc(CareerPlan::getCreateTime);
        return careerPlanMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
