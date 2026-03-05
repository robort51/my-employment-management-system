package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.entity.AiResumeRecord;

public interface AiResumeService {

    /** AI简历润色 */
    AiResumeRecord polish(Long studentId, String originalContent);

    /** 基于已上传简历进行 AI 润色 */
    AiResumeRecord polishByResume(Long studentId);

    /** 查看润色记录列表 */
    IPage<AiResumeRecord> getRecords(Long studentId, int pageNum, int pageSize);
}
