package com.example.controller;

import com.example.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器 - 统一处理校验错误
 *
 * 为什么需要全局异常处理？
 * 当 Controller 中的参数校验失败时，Spring 会抛出不同的异常：
 * 1. MethodArgumentNotValidException：@RequestBody 参数校验失败
 * 2. ConstraintViolationException：@RequestParam 等参数校验失败
 * 3. WebExchangeBindException：响应式编程中的参数校验失败
 * 4. MethodArgumentTypeMismatchException：参数类型不匹配
 *
 * 如果不处理这些异常，Spring Boot 会返回默认的错误页面。
 * 通过 @RestControllerAdvice 可以统一捕获并返回格式化的错误信息。
 *
 * 异常处理流程：
 * 1. 异常被抛出
 * 2. 被 @ExceptionHandler 匹配
 * 3. 构建 ApiResponse 返回给前端
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== @RequestBody 校验失败处理 ====================

    /**
     * 处理 @RequestBody 参数校验失败
     *
     * 触发场景：
     * - @Valid @RequestBody User user 校验失败时
     * - 抛出 MethodArgumentNotValidException
     *
     * 错误信息结构：
     * - 包含所有字段的错误信息
     * - 错误路径格式：object.field（如 user.username）
     * - 包含错误值（rejectedValue）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        System.out.println("========================================");
        System.out.println("捕获 MethodArgumentNotValidException");
        System.out.println("========================================");

        // 从异常中获取所有字段错误
        List<ApiResponse.FieldError> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            System.out.println("字段错误：" + fieldError.getField() +
                              " -> " + fieldError.getDefaultMessage());

            // 构建 FieldError 对象
            ApiResponse.FieldError error = new ApiResponse.FieldError();
            error.setField(fieldError.getField());
            error.setFieldName(fieldError.getField());
            error.setMessage(fieldError.getDefaultMessage());
            error.setRejectedValue(fieldError.getRejectedValue());
            error.setErrorCode(fieldError.getCode());

            errors.add(error);
        });

        // 打印全局错误（不属于特定字段的错误）
        ex.getBindingResult().getGlobalErrors().forEach(globalError -> {
            System.out.println("全局错误：" + globalError.getObjectName() +
                              " -> " + globalError.getDefaultMessage());
        });

        // 返回统一的错误响应
        return ApiResponse.<Void>badRequest("参数校验失败")
                .withErrors(errors);
    }

    // ==================== @RequestParam 等参数校验失败处理 ====================

    /**
     * 处理 @RequestParam、@PathVariable 等参数校验失败
     *
     * 触发场景：
     * - @Validated @RequestParam Integer age 校验失败时
     * - @Validated @PathVariable Long id 校验失败时
     * - 抛出 ConstraintViolationException
     *
     * 错误信息结构：
     * - 包含参数路径和错误信息
     * - 参数路径格式：methodName.parameterName（如 getUserById.id）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleConstraintViolationException(
            ConstraintViolationException ex) {

        System.out.println("========================================");
        System.out.println("捕获 ConstraintViolationException");
        System.out.println("========================================");

        // 从异常中获取所有约束违反
        List<ApiResponse.FieldError> errors = ex.getConstraintViolations().stream()
                .map(this::convertToFieldError)
                .collect(Collectors.toList());

        // 打印错误信息
        ex.getConstraintViolations().forEach(violation -> {
            System.out.println("约束违反：" + violation.getPropertyPath() +
                              " -> " + violation.getMessage());
        });

        return ApiResponse.<Void>badRequest("参数校验失败")
                .withErrors(errors);
    }

    // ==================== 响应式编程参数校验失败处理 ====================

    /**
     * 处理 WebFlux 响应式编程中的参数校验失败
     *
     * 触发场景：
     * - 使用 Spring WebFlux 时
     * - @RequestBody 参数校验失败
     * - 抛出 WebExchangeBindException
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleWebExchangeBindException(
            WebExchangeBindException ex) {

        System.out.println("========================================");
        System.out.println("捕获 WebExchangeBindException");
        System.out.println("========================================");

        List<ApiResponse.FieldError> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            System.out.println("字段错误：" + fieldError.getField() +
                              " -> " + fieldError.getDefaultMessage());

            ApiResponse.FieldError error = new ApiResponse.FieldError();
            error.setField(fieldError.getField());
            error.setFieldName(fieldError.getField());
            error.setMessage(fieldError.getDefaultMessage());
            error.setRejectedValue(fieldError.getRejectedValue());
            error.setErrorCode(fieldError.getCode());

            errors.add(error);
        });

        return ApiResponse.<Void>badRequest("参数校验失败")
                .withErrors(errors);
    }

    // ==================== 参数类型不匹配处理 ====================

    /**
     * 处理参数类型不匹配
     *
     * 触发场景：
     * - @PathVariable Long id 传入 "abc" 时
     * - @RequestParam Integer page 传入 "xyz" 时
     * - 抛出 MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {

        System.out.println("========================================");
        System.out.println("捕获 MethodArgumentTypeMismatchException");
        System.out.println("========================================");

        String parameterName = ex.getName();
        String requiredType = ex.getRequiredType() != null ?
                ex.getRequiredType().getSimpleName() : "未知类型";
        Object submittedValue = ex.getValue();

        System.out.println("参数不匹配：参数名=" + parameterName +
                          ", 需要类型=" + requiredType +
                          ", 提交值=" + submittedValue);

        ApiResponse.FieldError error = new ApiResponse.FieldError();
        error.setField(parameterName);
        error.setFieldName(parameterName);
        error.setMessage("参数类型不正确，需要 " + requiredType + " 类型");
        error.setRejectedValue(submittedValue);
        error.setErrorCode("TypeMismatch");

        List<ApiResponse.FieldError> errors = new ArrayList<>();
        errors.add(error);

        return ApiResponse.<Void>badRequest("参数类型不匹配")
                .withErrors(errors);
    }

    // ==================== 辅助方法 ====================

    /**
     * 将 ConstraintViolation 转换为 FieldError
     *
     * ConstraintViolation 包含：
     * - propertyPath：属性路径
     * - message：错误消息
     * - invalidValue：无效的值
     * - constraintDescriptor：约束描述
     */
    private ApiResponse.FieldError convertToFieldError(
            ConstraintViolation<?> violation) {

        // 提取参数名称（从 propertyPath 中）
        String parameterName = extractParameterName(violation.getPropertyPath());

        ApiResponse.FieldError error = new ApiResponse.FieldError();
        error.setField(parameterName);
        error.setFieldName(parameterName);
        error.setMessage(violation.getMessage());
        error.setRejectedValue(violation.getInvalidValue());

        // 获取错误码（注解名称，如 NotBlank、Size 等）
        String errorCode = extractErrorCode(violation);
        error.setErrorCode(errorCode);

        return error;
    }

    /**
     * 从 PropertyPath 中提取参数名称
     *
     * PropertyPath 示例：
     * - getUserById.id -> id
     * - verifyPhone.phone -> phone
     * - batchCreate.names[0] -> names[0]
     */
    private String extractParameterName(Path propertyPath) {
        StringBuilder sb = new StringBuilder();

        for (Path.Node node : propertyPath) {
            if (node.getKind() == ElementKind.METHOD ||
                node.getKind() == ElementKind.CONSTRUCTOR) {
                // 跳过方法名和构造函数名
                continue;
            }

            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(node.getName());
        }

        return sb.toString();
    }

    /**
     * 从 ConstraintViolation 中提取错误码
     *
     * 错误码通常是注解的简单名称，如：
     * - NotBlank
     * - Size
     * - Min
     * - Email
     */
    private String extractErrorCode(ConstraintViolation<?> violation) {
        String annotationName = violation.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .getSimpleName();

        return annotationName;
    }

    // ==================== 通用异常处理 ====================

    /**
     * 处理所有未捕获的异常
     *
     * 这是一个兜底的异常处理器，
     * 建议在最后添加，捕获所有其他处理器没有处理的异常。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGenericException(Exception ex) {
        System.out.println("========================================");
        System.out.println("捕获未处理异常：" + ex.getClass().getName());
        System.out.println("========================================");
        System.out.println("异常消息：" + ex.getMessage());
        ex.printStackTrace();

        return ApiResponse.serverError("服务器内部错误：" + ex.getMessage());
    }
}
