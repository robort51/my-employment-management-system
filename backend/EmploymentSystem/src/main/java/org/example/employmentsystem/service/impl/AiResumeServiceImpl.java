package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.entity.AiResumeRecord;
import org.example.employmentsystem.mapper.AiResumeRecordMapper;
import org.example.employmentsystem.service.AiResumeService;
import org.example.employmentsystem.service.AiService;
import org.springframework.stereotype.Service;

/**
 * AI简历润色 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class AiResumeServiceImpl implements AiResumeService {

    private final AiResumeRecordMapper aiResumeRecordMapper;
    private final AiService aiService;

    private static final String SYSTEM_PROMPT = """
            你是一名专业的HR顾问和简历优化专家。请对用户提供的简历内容进行润色和优化。
            要求：
            1. 保持原有信息不变，优化措辞和表达
            2. 使用STAR法则优化项目经验描述
            3. 量化工作成果（加入具体数字）
            4. 突出关键技能和核心竞争力
            5. 使语言更加专业、简洁、有力
            6. 直接返回润色后的简历内容，不要多余的解释
            """;

    @Override
    public AiResumeRecord polish(Long studentId, String originalContent) {
        // 调用 AI 润色
        String polishedContent = aiService.chat(SYSTEM_PROMPT,
                "请帮我润色以下简历内容：\n\n" + originalContent);

        // 保存记录
        AiResumeRecord record = new AiResumeRecord();
        record.setStudentId(studentId);
        record.setOriginalContent(originalContent);
        record.setPolishedContent(polishedContent);
        aiResumeRecordMapper.insert(record);

        return record;
    }

    @Override
    public IPage<AiResumeRecord> getRecords(Long studentId, int pageNum, int pageSize) {
        LambdaQueryWrapper<AiResumeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiResumeRecord::getStudentId, studentId)
               .orderByDesc(AiResumeRecord::getCreateTime);
        return aiResumeRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
