package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.entity.AiInterviewRecord;

public interface AiInterviewService {

    /** 生成模拟面试题目 */
    AiInterviewRecord generateQuestions(Long studentId, String jobTitle);

    /** 提交答案并获取AI评价和评分 */
    AiInterviewRecord submitAnswers(Long recordId, Long studentId, String answers);

    /** 查看模拟面试记录列表 */
    IPage<AiInterviewRecord> getRecords(Long studentId, int pageNum, int pageSize);

    /** 查看单条记录详情 */
    AiInterviewRecord getById(Long id);
}
