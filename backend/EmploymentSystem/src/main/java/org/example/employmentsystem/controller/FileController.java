package org.example.employmentsystem.controller;

import org.example.employmentsystem.common.BusinessException;
import org.example.employmentsystem.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 * 将文件存到服务器本地 uploads/ 目录，返回访问URL
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${spring.servlet.multipart.location:uploads}")
    private String uploadDir;

    /**
     * 上传文件
     * POST /api/file/upload
     * 返回文件访问路径
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 生成唯一文件名，防止重名覆盖
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 确保目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        try {
            File dest = new File(dir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }

        // 返回访问路径（前端通过这个URL访问文件）
        String fileUrl = "/uploads/" + newFilename;
        return Result.success("上传成功", fileUrl);
    }
}
