package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.model.Address;
import com.example.model.RegisterRequest;
import com.example.model.User;
import com.example.validator.CustomValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器 - 演示 Spring Validation 的各种使用场景
 *
 * 本控制器展示了：
 * 1. @Validated 和 @Valid 的区别
 * 2. 简单参数校验
 * 3. 对象参数校验（@Valid）
 * 4. 分组校验
 * 5. 嵌套对象校验（@Valid）
 * 6. 自定义校验器
 * 7. 校验错误消息的获取和处理
 *
 * @Validated vs @Valid 的区别：
 * 1. @Validated 是 Spring 提供的注解，@Valid 是 JSR-303 提供的注解
 * 2. @Validated 可以用于类级别和方法参数，@Valid 只能用于方法参数
 * 3. @Validated 支持分组校验（通过 groups 属性），@Valid 不支持
 * 4. @Validated 在类级别使用会触发类中所有 @Valid 方法的校验
 * 5. @Valid 可以用于嵌套对象的递归校验，@Validated 不行
 */
@RestController
@RequestMapping("/api/user")
/**
 * @Validated 注解放在类级别
 * 效果：类中所有方法参数都会进行基础校验（但不支持分组）
 * 注意：类级别的 @Validated 配合方法级别的 @Valid 使用
 */
@Validated
public class UserController {

    // ==================== @Validated vs @Valid 区别演示 ====================

    /**
     * 演示 @Valid 用于嵌套对象校验
     *
     * @Valid 的特点：
     * 1. 是 JSR-303 标准注解
     * 2. 用于标记需要递归校验的嵌套对象
     * 3. 放在对象参数前
     *
     * 校验流程：
     * 1. Spring MVC 收到请求
     * 2. 发现参数有 @Valid，自动触发校验
     * 3. 校验 User 对象上的所有校验注解
     * 4. 如果 User 对象中有 @Valid 标记的嵌套对象（如 Address），
     *    会自动递归校验嵌套对象
     * 5. 如果有校验错误，抛出 MethodArgumentNotValidException
     */
    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody User user) {
        System.out.println("接收到注册请求：" + user);

        // 模拟注册成功
        user.setId(1L);
        return ApiResponse.success(user, "用户注册成功");
    }

    /**
     * 演示 @Validated 用于方法参数校验
     *
     * @Validated 的特点：
     * 1. 是 Spring 提供的注解
     * 2. 支持分组校验
     * 3. 可以放在类级别和方法参数前
     *
     * 当 @Validated 放在方法参数前时：
     * 1. Spring 会对参数进行校验
     * 2. 支持分组校验（通过 groups 属性指定）
     * 3. 如果校验失败，抛出 ConstraintViolationException
     */
    @PostMapping("/create")
    public ApiResponse<RegisterRequest> create(
            // 使用 groups 属性指定分组校验
            @Validated(RegisterRequest.CreateGroup.class) @RequestBody RegisterRequest request) {
        System.out.println("接收到创建用户请求：" + request);

        // 模拟创建成功
        return ApiResponse.success(request, "用户创建成功");
    }

    /**
     * 演示更新用户时的分组校验
     *
     * 分组校验的规则：
     * 1. 如果不指定 groups，则属于默认分组（Default）
     * 2. 如果指定了 groups，则只在指定的分组中校验
     * 3. 调用校验时，可以指定校验哪些分组
     *
     * 例如：
     * - 注册时需要校验密码、确认密码、协议确认
     * - 更新时不需要校验密码（密码可能为空或不修改）
     */
    @PostMapping("/update")
    public ApiResponse<RegisterRequest> update(
            @Validated(RegisterRequest.UpdateGroup.class) @RequestBody RegisterRequest request) {
        System.out.println("接收到更新用户请求：" + request);

        // 模拟更新成功
        return ApiResponse.success(request, "用户更新成功");
    }

    // ==================== 简单参数校验演示 ====================

    /**
     * 演示简单参数校验（不使用对象）
     *
     * 当使用 @Validated 注解在方法参数上时：
     * 1. Spring 会对标注了校验注解的参数进行校验
     * 2. 如果校验失败，抛出 ConstraintViolationException
     *
     * 常用的参数校验注解：
     * - @NotNull：不能为 null
     * - @NotBlank：不能为 null、不能是空字符串、不能是空白字符
     * - @NotEmpty：不能为 null 且不能为空（集合、数组、字符串）
     * - @Min/@Max：数值最小/最大值
     * - @DecimalMin/@DecimalMax：数值的最小/最大值（支持小数）
     * - @Size：长度/大小范围
     * - @Email：邮箱格式
     * - @Pattern：正则表达式
     * - @Positive/@PositiveOrZero：正数/非负数
     * - @Past/@Future：过去/未来日期
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(
            // @PathVariable 标记路径变量，也会受到 @Validated 校验
            @PathVariable @NotNull(message = "用户ID不能为空") Long id,

            // @RequestParam 标记查询参数，也会被校验
            @RequestParam(required = false) @Min(value = 1, message = "页码最小为1") Integer page,
            @RequestParam(required = false) @Max(value = 100, message = "每页最大100条") Integer size) {

        System.out.println("查询用户，ID：" + id + ", page：" + page + ", size：" + size);

        // 模拟查询结果
        User user = new User();
        user.setId(id);
        user.setUsername("user" + id);
        user.setNickname("用户" + id);
        user.setEmail("user" + id + "@example.com");

        return ApiResponse.success(user);
    }

    /**
     * 演示 @Email 邮箱格式校验
     */
    @GetMapping("/email/{email}")
    public ApiResponse<String> checkEmail(
            @PathVariable @Email(message = "邮箱格式不正确") String email) {

        System.out.println("检查邮箱：" + email);
        return ApiResponse.success(email, "邮箱格式正确");
    }

    /**
     * 演示 @Pattern 正则表达式校验
     */
    @GetMapping("/phone/{phone}")
    public ApiResponse<String> validatePhone(
            @PathVariable @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {

        System.out.println("校验手机号：" + phone);
        return ApiResponse.success(phone, "手机号格式正确");
    }

    /**
     * 演示日期校验 @Past 和 @Future
     */
    @PostMapping("/appointment")
    public ApiResponse<Map<String, String>> createAppointment(
            @RequestParam @Past(message = "预约日期必须是过去的时间") LocalDate pastDate,
            @RequestParam @Future(message = "预约日期必须是未来的时间") LocalDate futureDate) {

        System.out.println("预约日期（过去）：" + pastDate);
        System.out.println("预约日期（未来）：" + futureDate);

        Map<String, String> result = new HashMap<>();
        result.put("pastDate", pastDate != null ? pastDate.toString() : "null");
        result.put("futureDate", futureDate != null ? futureDate.toString() : "null");

        return ApiResponse.success(result, "预约日期设置成功");
    }

    // ==================== 自定义校验器演示 ====================

    /**
     * 演示自定义校验器
     *
     * @CustomValidator 是我们自定义的校验注解
     * 校验规则包括：
     * 1. 非空校验（根据 required 属性）
     * 2. 长度校验（根据 min 和 max 属性）
     * 3. 字符校验（不能包含空格、<、> 等）
     * 4. 数值校验（如果是数字，则在指定范围内）
     */
    @PostMapping("/validate")
    public ApiResponse<String> validateCustom(
            @RequestBody @CustomValidator(required = true, min = 3, max = 20) String value) {

        System.out.println("自定义校验值：" + value);
        return ApiResponse.success(value, "自定义校验通过");
    }

    // ==================== 列表元素校验演示 ====================

    /**
     * 演示 @Size 校验集合大小
     */
    @PostMapping("/batch")
    public ApiResponse<List<String>> batchCreate(
            @RequestBody @Size(min = 1, max = 10, message = "批量操作数量必须在1-10之间") List<@NotBlank(message = "名称不能为空") String> names) {

        System.out.println("批量创建，数量：" + names.size());
        return ApiResponse.success(names, "批量创建成功");
    }

    // ==================== 数值范围校验演示 ====================

    /**
     * 演示数值范围校验
     *
     * @Min/@Max：整数最小/最大值
     * @DecimalMin/@DecimalMax：小数的最小/最大值
     * @Positive/@Negative：正数/负数
     * @PositiveOrZero/@NegativeOrZero：非负数/非正数
     */
    @PostMapping("/balance")
    public ApiResponse<Map<String, Object>> updateBalance(
            @RequestParam @DecimalMin(value = "0.01", message = "最小转账金额为0.01") BigDecimal amount,
            @RequestParam @DecimalMax(value = "999999.99", message = "单笔转账金额不能超过999999.99") BigDecimal maxAmount,
            @RequestParam @Positive(message = "积分必须为正数") Integer points) {

        System.out.println("转账金额：" + amount + ", 最大金额：" + maxAmount + ", 积分：" + points);

        Map<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("maxAmount", maxAmount);
        result.put("points", points);

        return ApiResponse.success(result, "金额设置成功");
    }

    /**
     * 演示 @Digits 校验数值格式
     *
     * @Digits(integer, fraction)：
     * - integer：整数部分最多几位
     * - fraction：小数部分最多几位
     */
    @PostMapping("/price")
    public ApiResponse<BigDecimal> setPrice(
            @RequestParam @Digits(integer = 10, fraction = 2, message = "价格格式不正确，整数部分最多10位，小数部分最多2位") BigDecimal price) {

        System.out.println("设置价格：" + price);
        return ApiResponse.success(price, "价格设置成功");
    }

    // ==================== 布尔校验演示 ====================

    /**
     * 演示布尔校验 @AssertTrue 和 @AssertFalse
     *
     * @AssertTrue：值必须为 true
     * @AssertFalse：值必须为 false
     * 注意：值为 null 时会通过校验（null 不是 true 也不是 false）
     */
    @PostMapping("/agree")
    public ApiResponse<Boolean> agreeTerms(
            @RequestParam @AssertTrue(message = "必须同意用户协议") Boolean agree) {

        System.out.println("是否同意协议：" + agree);
        return ApiResponse.success(agree, "操作成功");
    }

    // ==================== 综合演示 ====================

    /**
     * 综合演示：完整的用户信息校验
     *
     * 这个接口演示了：
     * 1. 多字段校验
     * 2. 嵌套对象校验（@Valid）
     * 3. 各种类型的校验注解
     */
    @PostMapping("/full")
    public ApiResponse<User> createUserFull(
            @Valid @RequestBody User user) {

        System.out.println("接收到完整用户信息：" + user);

        // 模拟创建成功
        user.setId(System.currentTimeMillis());
        return ApiResponse.success(user, "用户创建成功");
    }

    /**
     * 模拟验证手机号接口
     * 演示 @Pattern 的使用
     */
    @GetMapping("/verify/{phone}")
    public ApiResponse<Map<String, Object>> verifyPhone(
            @PathVariable @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
            @RequestParam @NotBlank(message = "验证码不能为空") String code) {

        System.out.println("验证手机号：" + phone + ", 验证码：" + code);

        Map<String, Object> result = new HashMap<>();
        result.put("phone", phone);
        result.put("verified", true);

        return ApiResponse.success(result, "验证成功");
    }

    /**
     * 模拟设置生日接口
     * 演示 @Past 日期校验
     */
    @PostMapping("/birthday")
    public ApiResponse<LocalDate> setBirthday(
            @RequestParam @Past(message = "生日必须是过去的时间") LocalDate birthDate) {

        System.out.println("设置生日：" + birthDate);
        return ApiResponse.success(birthDate, "生日设置成功");
    }

    /**
     * 模拟查询时间段接口
     * 演示 @PastOrPresent 和 @FutureOrPresent
     */
    @GetMapping("/period")
    public ApiResponse<Map<String, LocalDate>> queryPeriod(
            @RequestParam @PastOrPresent(message = "开始日期不能是未来时间") LocalDate startDate,
            @RequestParam @FutureOrPresent(message = "结束日期不能是过去时间") LocalDate endDate) {

        System.out.println("查询时间段：" + startDate + " ~ " + endDate);

        Map<String, LocalDate> result = new HashMap<>();
        result.put("startDate", startDate);
        result.put("endDate", endDate);

        return ApiResponse.success(result, "查询成功");
    }
}
