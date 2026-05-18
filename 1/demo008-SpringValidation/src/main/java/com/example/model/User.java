package com.example.model;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类 - 演示 JSR-303/JSR-380 校验注解
 *
 * JSR-303/JSR-380 是 Java Bean Validation 规范，定义了一套用于校验 JavaBean 的注解。
 * Spring Validation 实现了这些规范，让开发者可以在实体类上声明校验规则。
 *
 * 常用校验注解分类：
 * 1. 不能为空校验：@NotNull, @NotBlank, @NotEmpty
 * 2. 长度校验：@Size, @Length
 * 3. 数值范围校验：@Min, @Max, @DecimalMin, @DecimalMax
 * 4. 格式校验：@Email, @Pattern, @Digits
 * 5. 日期校验：@Past, @Future
 * 6. 布尔校验：@AssertTrue, @AssertFalse
 * 7. 嵌套校验：@Valid
 */
public class User {

    // ==================== 不能为空校验 ====================

    /**
     * @NotNull
     * 作用：校验对象不能为 null
     * 适用类型：所有类型，包括包装类型、对象、集合
     * 注意：@NotNull 不会校验空字符串 ""，只会校验 null
     * 示例：@NotNull(message = "用户名不能为空")
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * @NotBlank
     * 作用：校验字符串不能为 null、不能是空字符串（""）、不能只有空白字符
     * 适用类型：CharSequence（字符串类型）
     * 注意：会调用 trim().length() > 0 来判断
     * 示例：@NotBlank(message = "用户名不能为空")
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * @NotEmpty
     * 作用：校验集合/数组/字符串不能为 null 且不能为空
     * 适用类型：Collection（集合）、Map、数组、CharSequence
     * 注意：
     *   - 对字符串：等价于 @NotBlank
     *   - 对集合/数组：校验 size() > 0
     *   - 对 Map：校验 isEmpty() == false
     * 示例：@NotEmpty(message = "密码不能为空")
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

    // ==================== 长度校验 ====================

    /**
     * @Size
     * 作用：校验元素的大小/长度必须在指定范围内
     * 适用类型：Collection（集合）、Map、数组、CharSequence
     * 参数：
     *   - min：最小长度/大小，默认 0
     *   - max：最大长度/大小，默认 Long.MAX_VALUE
     * 示例：@Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
     */
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20个字符之间")
    private String nickname;

    /**
     * @Length（Hibernate Validator 特有，Spring Validation 也能用）
     * 作用：校验字符串长度
     * 适用类型：CharSequence
     * 参数：min：最小长度，max：最大长度
     * 注意：这是 Hibernate Validator 的扩展注解，功能与 @Size 类似
     */
    @org.hibernate.validator.constraints.Length(min = 11, max = 11, message = "手机号必须为11位")
    private String phone;

    // ==================== 数值范围校验 ====================

    /**
     * @Min
     * 作用：校验数值必须大于或等于指定值
     * 适用类型：BigDecimal、BigInteger、byte、short、int、long 及对应包装类型
     * 注意：只校验 >= 传入的值，不会自动处理类型转换
     * 示例：@Min(value = 0, message = "年龄不能小于0")
     */
    @Min(value = 0, message = "年龄不能小于0岁")
    private Integer age;

    /**
     * @Max
     * 作用：校验数值必须小于或等于指定值
     * 适用类型：与 @Min 相同
     * 示例：@Max(value = 150, message = "年龄不能超过150岁")
     */
    @Max(value = 150, message = "年龄不能超过150岁")
    private Integer maxAge;

    /**
     * @DecimalMin
     * 作用：校验数值必须大于或等于指定值（支持小数）
     * 适用类型：BigDecimal、String
     * 参数：
     *   - value：最小值（字符串形式，支持小数）
     *   - inclusive：是否包含边界值，默认 true
     * 示例：@DecimalMin(value = "0.01", message = "金额不能小于0.01")
     */
    @DecimalMin(value = "0.00", inclusive = true, message = "账户余额不能为负数")
    private BigDecimal balance;

    /**
     * @DecimalMax
     * 作用：校验数值必须小于或等于指定值（支持小数）
     * 适用类型：与 @DecimalMin 相同
     * 示例：@DecimalMax(value = "999999.99", message = "单笔交易金额不能超过999999.99")
     */
    @DecimalMax(value = "999999.99", inclusive = true, message = "单笔交易金额不能超过999999.99")
    private BigDecimal maxTransactionAmount;

    /**
     * @Positive 和 @PositiveOrZero（JSR-380 新增）
     * 作用：@Positive 校验数值必须为正数，@PositiveOrZero 校验数值必须为正数或零
     * 适用类型：BigDecimal、BigInteger、byte、short、int、long、float、double 及对应包装类型
     * 示例：@Positive(message = "积分必须为正数")
     */
    @Positive(message = "积分必须为正数")
    private Integer points;

    /**
     * @Negative 和 @NegativeOrZero（JSR-380 新增）
     * 作用：@Negative 校验数值必须为负数，@NegativeOrZero 校验数值必须为负数或零
     * 示例：@NegativeOrZero(message = "折扣不能为正数")
     */
    @NegativeOrZero(message = "折扣不能为正数")
    private BigDecimal discount;

    // ==================== 格式校验 ====================

    /**
     * @Email
     * 作用：校验字符串是否为有效的邮箱格式
     * 适用类型：CharSequence
     * 注意：允许为空字符串通过，如果需要不能为空需配合 @NotBlank
     * 示例：@Email(message = "邮箱格式不正确")
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * @Pattern
     * 作用：校验字符串是否符合正则表达式
     * 适用类型：CharSequence
     * 参数：regexp：正则表达式，flags：正则标志数组
     * 示例：@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * @Digits（JSR-380 新增）
     * 作用：校验数值的整数部分和小数部分的位数
     * 参数：
     *   - integer：整数部分的最多位数
     *   - fraction：小数部分的最多位数
     * 示例：@Digits(integer = 6, fraction = 2, message = "金额格式不正确，整数部分最多6位，小数部分最多2位")
     */
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    private BigDecimal amount;

    // ==================== 日期校验 ====================

    /**
     * @Past
     * 作用：校验日期必须是将来的日期（早于当前时间）
     * 适用类型：Date、Calendar、LocalDate、LocalDateTime 等
     * 注意：JSR-310 中的 java.time 类型需要 @Past 或 @Future 注解
     * 示例：@Past(message = "出生日期必须是过去的时间")
     */
    @Past(message = "出生日期必须是过去的时间")
    private LocalDate birthDate;

    /**
     * @Future
     * 作用：校验日期必须是将来的日期（晚于当前时间）
     * 适用类型：与 @Past 相同
     * 示例：@Future(message = "预约日期必须是未来的时间")
     */
    @Future(message = "预约日期必须是未来的时间")
    private LocalDate appointmentDate;

    /**
     * @PastOrPresent（JSR-380 新增）
     * 作用：校验日期必须是过去或现在的时间
     * 示例：@PastOrPresent(message = "入职日期不能是未来时间")
     */
    @PastOrPresent(message = "入职日期不能是未来时间")
    private LocalDate hireDate;

    /**
     * @FutureOrPresent（JSR-380 新增）
     * 作用：校验日期必须是将来或现在的时间
     * 示例：@FutureOrPresent(message = "会员到期日期必须是现在或未来时间")
     */
    @FutureOrPresent(message = "会员到期日期必须是现在或未来时间")
    private LocalDate membershipExpiration;

    // ==================== 布尔校验 ====================

    /**
     * @AssertTrue
     * 作用：校验布尔值必须为 true
     * 适用类型：boolean、Boolean
     * 注意：字段值为 null 时会通过校验，需要配合 @NotNull 使用
     * 示例：@AssertTrue(message = "必须同意用户协议")
     */
    @AssertTrue(message = "必须同意用户协议")
    private Boolean agreeTerms;

    /**
     * @AssertFalse
     * 作用：校验布尔值必须为 false
     * 适用类型：与 @AssertTrue 相同
     * 示例：@AssertFalse(message = "用户状态异常")
     */
    @AssertFalse(message = "用户状态异常")
    private Boolean isLocked;

    // ==================== 嵌套校验 ====================

    /**
     * @Valid
     * 作用：标记需要进行嵌套校验的对象
     * 当校验 User 对象时，如果 address 字段不为 null，
     * 会自动递归校验 Address 对象上的所有校验注解
     *
     * 注意：
     * 1. @Valid 是 JSR-303 规范中的注解
     * 2. Spring 还提供了 @Validated 注解用于分组校验
     * 3. 嵌套的对象必须被 @Valid 标注才会触发递归校验
     * 4. 嵌套校验的路径会包含在错误信息中，如 address.city
     */
    @Valid
    private Address address;

    // ==================== Getter 和 Setter 方法 ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getMaxTransactionAmount() {
        return maxTransactionAmount;
    }

    public void setMaxTransactionAmount(BigDecimal maxTransactionAmount) {
        this.maxTransactionAmount = maxTransactionAmount;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getMembershipExpiration() {
        return membershipExpiration;
    }

    public void setMembershipExpiration(LocalDate membershipExpiration) {
        this.membershipExpiration = membershipExpiration;
    }

    public Boolean getAgreeTerms() {
        return agreeTerms;
    }

    public void setAgreeTerms(Boolean agreeTerms) {
        this.agreeTerms = agreeTerms;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", birthDate=" + birthDate +
                ", agreeTerms=" + agreeTerms +
                '}';
    }
}
