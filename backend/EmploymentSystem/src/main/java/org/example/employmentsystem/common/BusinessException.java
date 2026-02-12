package org.example.employmentsystem.common;

import lombok.Getter;

/**
 * 自定义业务异常
 * 在 Service 层中抛出，会被 GlobalExceptionHandler 捕获并返回统一格式
 * 用法：throw new BusinessException("用户名已存在");
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
