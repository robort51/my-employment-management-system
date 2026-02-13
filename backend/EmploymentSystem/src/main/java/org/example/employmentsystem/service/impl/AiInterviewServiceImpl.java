package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.entity.AiInterviewRecord;
import org.example.employmentsystem.mapper.AiInterviewRecordMapper;
import org.example.employmentsystem.service.AiInterviewService;
import org.example.employmentsystem.service.AiService;
import org.springframework.stereotype.Service;

/**
 * AI模拟面试 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class AiInterviewServiceImpl implements AiInterviewService {

    private final AiInterviewRecordMapper aiInterviewRecordMapper;
    private final AiService aiService;

    private static final String QUESTION_PROMPT = """
            你是一名资深的技术面试官。请根据用户提供的目标职位，生成5道面试题目。
            要求：
            1. 包含2道基础知识题、2道项目经验/场景题、1道开放性/综合题
            2. 题目难度适中，适合应届毕业生
            3. 返回格式为JSON数组，每个元素包含 "id"(1-5) 和 "question" 字段
            4. 只返回JSON数组，不要其他内容
            示例格式：[{"id":1,"question":"请介绍xxx"},{"id":2,"question":"请说明xxx"}]
            """;

    private static final String EVALUATE_PROMPT = """
            你是一名资深的技术面试官。请根据面试问答内容对候选人进行评价。
            要求：
            1. 对每道题的回答进行简要点评（优点和不足）
            2. 给出综合评价（整体表现、建议改进方向）
            3. 给出一个0-100的分数
            4. 返回格式为JSON对象，包含 "feedback"(综合评价文本) 和 "score"(数字) 两个字段
            5. 只返回JSON对象，不要其他内容
            示例格式：{"feedback":"综合评价内容...","score":75}
            """;

    @Override
    public AiInterviewRecord generateQuestions(Long studentId, String jobTitle) {
        // 调用 AI 生成面试题
        String questions = aiService.chat(QUESTION_PROMPT,
                "目标职位：" + jobTitle);

        // 保存记录（此时只有题目，还没有答案和评价）
        AiInterviewRecord record = new AiInterviewRecord();
        record.setStudentId(studentId);
        record.setJobTitle(jobTitle);
        record.setQaContent(questions);
        aiInterviewRecordMapper.insert(record);

        return record;
    }

    @Override
    public AiInterviewRecord submitAnswers(Long recordId, Long studentId, String answers) {
        AiInterviewRecord record = aiInterviewRecordMapper.selectById(recordId);
        if (record == null || !record.getStudentId().equals(studentId)) {
            throw new BusinessException("记录不存在或无权操作");
        }
        if (record.getAiFeedback() != null) {
            throw new BusinessException("该面试已评价，请勿重复提交");
        }

        // 构建评价请求
        String qaText = "面试题目：\n" + record.getQaContent() + "\n\n学生回答：\n" + answers;
        String evaluationJson = aiService.chat(EVALUATE_PROMPT, qaText);

        // 解析评价结果
        try {
            cn.hutool.json.JSONObject evaluation = cn.hutool.json.JSONUtil.parseObj(evaluationJson);
            record.setAiFeedback(evaluation.getStr("feedback"));
            record.setScore(evaluation.getInt("score"));
        } catch (Exception e) {
            // 如果 AI 返回格式不标准，直接存原文
            record.setAiFeedback(evaluationJson);
            record.setScore(0);
        }

        record.setQaContent(answers); // 更新为包含答案的完整问答
        aiInterviewRecordMapper.updateById(record);

        return record;
    }

    @Override
    public IPage<AiInterviewRecord> getRecords(Long studentId, int pageNum, int pageSize) {
        LambdaQueryWrapper<AiInterviewRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiInterviewRecord::getStudentId, studentId)
               .orderByDesc(AiInterviewRecord::getCreateTime);
        return aiInterviewRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public AiInterviewRecord getById(Long id) {
        return aiInterviewRecordMapper.selectById(id);
    }
}
