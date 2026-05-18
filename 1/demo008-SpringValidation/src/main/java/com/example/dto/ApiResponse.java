package com.example.dto;

import java.util.List;

/**
 * 统一 API 响应对象
 *
 * 为什么需要统一响应对象？
 * 在前后端分离的开发模式中，API 需要有统一的响应格式。
 * 不管是成功还是失败，都应该返回相同结构的响应。
 *
 * 统一响应格式的好处：
 * 1. 前端可以统一处理响应，无需判断响应结构
 * 2. 方便日志记录和监控
 * 3. 方便统一错误处理
 * 4. 方便 API 文档生成
 *
 * 响应结构：
 * - code：状态码，200 表示成功，其他表示错误
 * - message：提示信息，成功时为"操作成功"，失败时为错误描述
 * - data：响应数据，可以是任意类型
 * - errors：校验错误列表（仅校验失败时使用）
 */
public class ApiResponse<T> {

    /**
     * 响应状态码
     * 200：操作成功
     * 400：请求参数错误
     * 401：未授权
     * 403：禁止访问
     * 404：资源不存在
     * 500：服务器内部错误
     */
    private int code;

    /**
     * 响应消息
     * 成功时通常为"操作成功"
     * 失败时为具体的错误描述
     */
    private String message;

    /**
     * 响应数据
     * 泛型，可以是任意类型
     * 当 code 不为 200 时，data 可能为 null
     */
    private T data;

    /**
     * 校验错误列表
     * 仅当存在校验错误时使用
     * 包含字段路径、错误消息等信息
     */
    private List<FieldError> errors;

    /**
     * 时间戳
     * 用于记录响应时间，方便排查问题
     */
    private long timestamp;

    // ==================== 构造函数 ====================

    /**
     * 默认构造函数
     * 设置默认状态码为 200
     */
    public ApiResponse() {
        this.code = 200;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 带数据的构造函数
     */
    public ApiResponse(T data) {
        this();
        this.data = data;
        this.message = "操作成功";
    }

    /**
     * 带错误消息的构造函数
     */
    public ApiResponse(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>();
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    /**
     * 成功响应（带自定义消息）
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(data);
        response.setMessage(message);
        return response;
    }

    /**
     * 失败响应（带错误码和消息）
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message);
    }

    /**
     * 失败响应（使用默认错误码 500）
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message);
    }

    /**
     * 参数错误响应
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message);
    }

    /**
     * 未授权响应
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message);
    }

    /**
     * 服务器内部错误响应
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(500, message);
    }

    // ==================== 校验错误相关方法 ====================

    /**
     * 设置校验错误
     */
    public ApiResponse<T> withErrors(List<FieldError> errors) {
        this.errors = errors;
        this.code = 400;
        this.message = "参数校验失败";
        return this;
    }

    /**
     * 校验错误内部类
     * 用于描述具体字段的校验错误
     */
    public static class FieldError {
        /**
         * 字段路径
         * 如：user.username、address.city
         */
        private String field;

        /**
         * 字段中文名（用于显示）
         */
        private String fieldName;

        /**
         * 错误消息
         */
        private String message;

        /**
         * 错误值
         */
        private Object rejectedValue;

        /**
         * 错误码
         * 如：NotBlank、Size、Email 等
         */
        private String errorCode;

        public FieldError() {}

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public FieldError(String field, String fieldName, String message, Object rejectedValue, String errorCode) {
            this.field = field;
            this.fieldName = fieldName;
            this.message = message;
            this.rejectedValue = rejectedValue;
            this.errorCode = errorCode;
        }

        // Getter 和 Setter
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public String toString() {
            return "FieldError{" +
                    "field='" + field + '\'' +
                    ", fieldName='" + fieldName + '\'' +
                    ", message='" + message + '\'' +
                    ", rejectedValue=" + rejectedValue +
                    ", errorCode='" + errorCode + '\'' +
                    '}';
        }
    }

    // ==================== Getter 和 Setter ====================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errors=" + errors +
                ", timestamp=" + timestamp +
                '}';
    }
}
