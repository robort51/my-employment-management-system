package org.example.employmentsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.dto.ResumeDTO;
import org.example.employmentsystem.entity.Resume;
import org.example.employmentsystem.mapper.ResumeMapper;
import org.example.employmentsystem.service.OcrService;
import org.example.employmentsystem.service.ResumeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

/**
 * 简历 Service 实现类（一人一份简历）
 */
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final OcrService ocrService;

    @Value("${spring.servlet.multipart.location:uploads}")
    private String uploadDir;

    @Override
    public Resume getByStudentId(Long studentId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getStudentId, studentId);
        return resumeMapper.selectOne(wrapper);
    }

    @Override
    public void saveOrUpdate(Long studentId, ResumeDTO dto) {
        String content = normalizeContent(dto);
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("简历内容不能为空");
        }
        Resume resume = getByStudentId(studentId);
        if (resume == null) {
            resume = new Resume();
            resume.setStudentId(studentId);
            resume.setContent(content);
            resume.setImageUrl(dto.getImageUrl());
            resume.setOcrText(dto.getOcrText());
            resumeMapper.insert(resume);
        } else {
            resume.setContent(content);
            if (dto.getImageUrl() != null) {
                resume.setImageUrl(dto.getImageUrl());
            }
            if (dto.getOcrText() != null) {
                resume.setOcrText(dto.getOcrText());
            }
            resumeMapper.updateById(resume);
        }
    }

    @Override
    public Resume uploadImageAndRecognize(Long studentId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请上传简历图片");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase(Locale.ROOT);
        }
        if (!isSupportedImage(suffix)) {
            throw new BusinessException("仅支持 jpg/jpeg/png/webp/bmp 格式图片");
        }

        byte[] imageBytes;
        try {
            imageBytes = file.getBytes();
        } catch (IOException e) {
            throw new BusinessException("读取上传图片失败，请重试");
        }

        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException("创建上传目录失败");
        }
        File dest = new File(dir, filename);

        try {
            // 先 OCR，再落盘，避免 transferTo 后临时文件不可重复读取
            String ocrText = ocrService.recognizeText(imageBytes);
            file.transferTo(dest);
            String imageUrl = "/uploads/" + filename;

            Resume resume = getByStudentId(studentId);
            if (resume == null) {
                resume = new Resume();
                resume.setStudentId(studentId);
                resume.setImageUrl(imageUrl);
                resume.setOcrText(ocrText);
                resume.setContent(ocrText);
                resumeMapper.insert(resume);
            } else {
                resume.setImageUrl(imageUrl);
                resume.setOcrText(ocrText);
                resume.setContent(ocrText);
                resumeMapper.updateById(resume);
            }
            return resume;
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            throw new BusinessException("上传或识别失败，请稍后重试");
        }
    }

    private boolean isSupportedImage(String suffix) {
        return ".jpg".equals(suffix)
                || ".jpeg".equals(suffix)
                || ".png".equals(suffix)
                || ".webp".equals(suffix)
                || ".bmp".equals(suffix);
    }

    private String normalizeContent(ResumeDTO dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getContent() != null && !dto.getContent().isBlank()) {
            return dto.getContent();
        }
        if (dto.getOcrText() != null && !dto.getOcrText().isBlank()) {
            return dto.getOcrText();
        }
        return null;
    }
}
