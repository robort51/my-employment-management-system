package org.example.employmentsystem.common;

import lombok.Data;

/**
 * 统一响应结果封装
 * 所有接口统一返回此格式：{ code, message, data }
 */
@Data
public class Result<T> {

    private int code;       // 状态码：200成功，其他失败
    private String message; // 提示信息
    private T data;         // 返回数据

    private Result() {}

    /**
     * 成功 - 无数据
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功 - 带数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功 - 自定义消息 + 数据
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败 - 自定义消息
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 失败 - 自定义状态码和消息
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}
