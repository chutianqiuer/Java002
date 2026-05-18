package com.example.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义校验注解 - 演示如何创建自定义校验规则
 *
 * 为什么需要自定义校验器？
 * 内置的校验注解（如 @NotBlank、@Email 等）只能满足常见的校验场景。
 * 当业务逻辑需要更复杂的校验规则时，就需要自定义校验注解。
 *
 * 如何创建自定义校验注解？
 * 1. 创建一个注解接口，使用 @Constraint 指定校验器类
 * 2. 创建对应的 ConstraintValidator 实现类
 * 3. 在需要校验的字段上使用自定义注解
 *
 * 注解接口的必需属性：
 * - message：错误消息模板
 * - groups：分组，用于分组校验
 * - payload：负载，用于携带元数据
 */
@Documented
/**
 * @Target 指定注解可以使用的位置：
 * - ElementType.FIELD：字段
 * - ElementType.METHOD：方法
 * - ElementType.PARAMETER：参数
 * - ElementType.CONSTRUCTOR：构造函数
 * - ElementType.TYPE_USE：类型使用（Java 8+）
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
/**
 * @Retention 指定注解的生命周期：
 * - RetentionPolicy.RUNTIME：保留到运行时，可通过反射获取
 */
@Retention(RetentionPolicy.RUNTIME)
/**
 * @Constraint 标记这是一个校验注解
 * validatedBy 指定了处理此注解的校验器类
 */
@Constraint(validatedBy = {CustomValidatorImpl.class})
public @interface CustomValidator {

    /**
     * 错误消息模板
     * 可以使用 {validatedValue} 等占位符在校验器中替换
     * 使用格式：message = "{key}" 会从 messages.properties 中查找
     */
    String message() default "校验失败";

    /**
     * 分组数组
     * 用于分组校验，允许将校验规则分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载数组
     * 用于携带元数据给校验器
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 是否必须（用于演示自定义参数）
     */
    boolean required() default true;

    /**
     * 最小值（用于演示自定义参数）
     */
    int min() default 0;

    /**
     * 最大值（用于演示自定义参数）
     */
    int max() default Integer.MAX_VALUE;
}
