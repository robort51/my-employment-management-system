package org.example.employmentsystem.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 启动时自动补齐必要字段，避免手动执行 SQL 漏掉导致运行时报错。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaMigrationConfig {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrateResumeColumns() {
        try {
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            if (dbName == null || dbName.isBlank()) {
                return;
            }
            ensureColumn(
                    dbName,
                    "resume",
                    "image_url",
                    "ALTER TABLE resume ADD COLUMN image_url VARCHAR(255) NULL COMMENT '简历图片URL' AFTER content"
            );
            ensureColumn(
                    dbName,
                    "resume",
                    "ocr_text",
                    "ALTER TABLE resume ADD COLUMN ocr_text TEXT NULL COMMENT 'OCR识别文本' AFTER image_url"
            );
        } catch (Exception e) {
            log.error("自动迁移 resume 字段失败，请检查数据库权限或手动执行 migration_20260305_resume_image_ocr.sql", e);
        }
    }

    private void ensureColumn(String dbName, String tableName, String columnName, String alterSql) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class,
                dbName,
                tableName,
                columnName
        );
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.execute(alterSql);
        log.info("数据库字段自动迁移成功: {}.{}", tableName, columnName);
    }
}

