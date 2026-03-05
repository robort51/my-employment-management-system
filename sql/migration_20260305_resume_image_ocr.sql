-- 已有数据库请执行本脚本，为 resume 表增加图片简历与 OCR 字段
ALTER TABLE resume
    ADD COLUMN image_url VARCHAR(255) NULL COMMENT '简历图片URL' AFTER content,
    ADD COLUMN ocr_text TEXT NULL COMMENT 'OCR识别文本' AFTER image_url;

