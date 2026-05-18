package com.example.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义校验器实现类
 *
 * 如何实现自定义校验器？
 * 1. 实现 ConstraintValidator<A, T> 接口
 *    - A：自定义注解类型（如 CustomValidator）
 *    - T：要校验的类型（如 String、Integer 等）
 *
 * 2. 实现 initialize(A constraintAnnotation) 方法
 *    - 在此方法中初始化校验器，读取注解中的参数
 *
 * 3. 实现 isValid(T value, ConstraintValidatorContext context) 方法
 *    - 在此方法中实现具体的校验逻辑
 *    - 返回 true 表示校验通过
 *    - 返回 false 表示校验失败
 *    - 如果 value 为 null，通常应该返回 true（除非该字段是必填的）
 *
 * ConstraintValidatorContext 的作用：
 * - 用于构建自定义错误消息
 * - 用于禁用默认错误消息
 * - 用于添加自定义错误节点
 */
public class CustomValidatorImpl implements ConstraintValidator<CustomValidator, String> {

    /**
     * 是否必须
     */
    private boolean required;

    /**
     * 最小长度
     */
    private int min;

    /**
     * 最大长度
     */
    private int max;

    /**
     * 允许的值列表（用于演示多值校验）
     */
    private Set<String> allowedValues;

    /**
     * 初始化方法
     * 在校验器实例化时调用，用于读取注解中的参数
     */
    @Override
    public void initialize(CustomValidator constraintAnnotation) {
        // 读取注解中的 required 属性
        this.required = constraintAnnotation.required();
        // 读取注解中的 min 属性
        this.min = constraintAnnotation.min();
        // 读取注解中的 max 属性
        this.max = constraintAnnotation.max();

        // 初始化允许的值列表（可以在这里从数据库或其他地方加载）
        this.allowedValues = new HashSet<>(Arrays.asList(
            "admin", "user", "guest", "moderator"
        ));

        // 打印初始化信息，方便调试
        System.out.println("CustomValidator 初始化完成：required=" + required +
                          ", min=" + min + ", max=" + max);
    }

    /**
     * 校验方法
     *
     * @param value   要校验的值（可以是 null）
     * @param context 校验上下文，用于构建错误消息
     * @return true 校验通过，false 校验失败
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        System.out.println("CustomValidator 开始校验，值：" + value);

        // 如果值为 null，且 required 为 true，校验失败
        // 注意：这里的处理方式和 @NotNull 不同
        // @NotNull 会处理 null 值，而自定义校验需要自己判断
        if (value == null) {
            if (required) {
                System.out.println("CustomValidator 校验失败：值为 null");
                return false;
            }
            // 如果 required 为 false，null 值是允许的
            System.out.println("CustomValidator 校验通过：值为 null 但 required=false");
            return true;
        }

        // 1. 长度校验
        if (value.length() < min) {
            System.out.println("CustomValidator 校验失败：长度小于最小值 " + min);
            buildErrorMessage(context, "长度不能小于" + min);
            return false;
        }

        if (value.length() > max) {
            System.out.println("CustomValidator 校验失败：长度大于最大值 " + max);
            buildErrorMessage(context, "长度不能大于" + max);
            return false;
        }

        // 2. 检查是否包含非法字符（示例：不能包含空格和特殊字符）
        if (value.contains(" ") || value.contains("<") || value.contains(">")) {
            System.out.println("CustomValidator 校验失败：包含非法字符");
            buildErrorMessage(context, "不能包含空格和特殊字符 < >");
            return false;
        }

        // 3. 检查是否是允许的值（示例：只能是特定值）
        // 注意：这个校验可能与长度校验冲突，这里只是演示用
        // 如果 allowedValues 包含该值，则直接通过
        if (allowedValues.contains(value.toLowerCase())) {
            System.out.println("CustomValidator 校验通过：是允许的值");
            return true;
        }

        // 4. 如果 required 为 true，则非空字符串必须符合某些规则
        // 这里演示：如果是纯数字，则必须是在指定范围内
        if (value.matches("\\d+")) {
            try {
                int numValue = Integer.parseInt(value);
                if (numValue < min || numValue > max) {
                    System.out.println("CustomValidator 校验失败：数字超出范围");
                    buildErrorMessage(context, "数字必须在" + min + "到" + max + "之间");
                    return false;
                }
            } catch (NumberFormatException e) {
                // 转换失败，不进行数值范围校验
            }
        }

        System.out.println("CustomValidator 校验通过");
        return true;
    }

    /**
     * 构建自定义错误消息
     *
     * 默认的错误消息是注解中定义的 message() 默认值。
     * 使用 ConstraintValidatorContext 可以动态构建错误消息。
     */
    private void buildErrorMessage(ConstraintValidatorContext context, String message) {
        // 禁用默认的错误消息
        context.disableDefaultConstraintViolation();

        // 添加自定义错误消息
        // 第二个参数是错误节点的名称，null 表示根节点
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintNode(null)
               .addBeanNode()
               .inIterable()
               .atIndex(null)
               .addConstraintViolation();
    }
}
