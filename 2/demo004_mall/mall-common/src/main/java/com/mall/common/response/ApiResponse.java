package com.mall.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Unified API response wrapper
 */
@Data
public class ApiResponse<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ERROR_CODE);
        response.setMessage(message);
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
