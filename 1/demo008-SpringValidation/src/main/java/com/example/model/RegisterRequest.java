package com.example.model;

import javax.validation.constraints.*;
import java.util.List;

/**
 * 注册请求对象 - 演示分组校验
 *
 * 分组校验是什么？
 * 在实际开发中，同一个实体类在不同的操作场景下需要校验不同的字段。
 * 例如：用户注册时需要校验密码，但用户更新时不需要修改密码就不应该校验密码。
 * 分组校验就是为了解决这种场景而设计的。
 *
 * 如何使用分组校验？
 * 1. 定义分组接口（空接口，继承 Default）
 * 2. 在 @NotNull、@NotBlank 等注解中指定 groups 属性
 * 3. 调用校验时指定要校验的分组
 *
 * 分组接口命名建议：
 * - CreateGroup：创建时需要校验的字段
 * - UpdateGroup：更新时需要校验的字段
 * - DeleteGroup：删除时需要校验的字段
 * - ViewGroup：查看时需要校验的字段
 */
public class RegisterRequest {

    // ==================== 基础信息（注册和更新都需要校验） ====================

    /**
     * 用户名
     * 两种场景都需要校验：
     * - 注册时：必须填写，且不能重复
     * - 更新时：如果允许修改用户名，也需要校验
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    /**
     * 邮箱
     * 注册时必须填写且格式正确
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     * 可选字段，但如果有值必须符合格式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    // ==================== 密码（仅注册时需要校验） ====================

    /**
     * 密码
     * 只有注册时才需要校验，更新时可能为空（不修改密码）
     *
     * 分组说明：
     * - groups = {CreateGroup.class} 表示只在 CreateGroup 分组校验时生效
     * - 如果不指定 groups，则在所有分组中都会校验
     */
    @NotBlank(message = "密码不能为空", groups = {CreateGroup.class})
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间", groups = {CreateGroup.class})
    private String password;

    /**
     * 确认密码
     * 仅注册时需要校验，用于两次密码输入一致性校验
     */
    @NotBlank(message = "确认密码不能为空", groups = {CreateGroup.class})
    private String confirmPassword;

    // ==================== 个人资料（可选字段） ====================

    /**
     * 昵称
     * 可选字段，但如果有值需要符合长度要求
     */
    @Size(min = 2, max = 30, message = "昵称长度必须在2-30个字符之间")
    private String nickname;

    /**
     * 年龄
     * 可选字段，但如果有值需要在合理范围内
     */
    @Min(value = 0, message = "年龄不能小于0岁")
    @Max(value = 150, message = "年龄不能超过150岁")
    private Integer age;

    /**
     * 头像URL
     * 可选字段，但如果有值需要是有效的URL格式
     */
    @Pattern(regexp = "^(https?://)?[\\w.-]+(?:\\.[\\w.-]+)+[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$|^$",
             message = "头像URL格式不正确")
    private String avatarUrl;

    // ==================== 协议确认（仅注册时需要勾选） ====================

    /**
     * 是否同意用户协议
     * 仅注册时需要确认
     */
    @AssertTrue(message = "必须同意用户协议才能注册", groups = {CreateGroup.class})
    private Boolean agreeTerms;

    // ==================== 兴趣爱好列表（可选） ====================

    /**
     * 兴趣爱好
     * 可选字段，如果有值则不能为空列表
     */
    @NotEmpty(message = "兴趣爱好不能为空", groups = {CreateGroup.class})
    private List<@NotBlank(message = "兴趣爱好项不能为空") String> hobbies;

    // ==================== Getter 和 Setter 方法 ====================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getAgreeTerms() {
        return agreeTerms;
    }

    public void setAgreeTerms(Boolean agreeTerms) {
        this.agreeTerms = agreeTerms;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", age=" + age +
                ", agreeTerms=" + agreeTerms +
                ", hobbies=" + hobbies +
                '}';
    }

    /**
     * 创建分组接口
     * 用于标识创建操作需要校验的字段
     *
     * 分组接口规范：
     * 1. 必须是接口（不能是类）
     * 2. 建议继承 Default 分组，以便在需要时同时校验默认分组
     * 3. 命名要清晰，通常以 Group 结尾
     */
    public interface CreateGroup extends Default {
        // CreateGroup 继承自 Default，在校验 CreateGroup 分组时
        // 也会自动包含所有没有指定分组的字段（默认分组）
    }

    /**
     * 更新分组接口
     * 用于标识更新操作需要校验的字段
     */
    public interface UpdateGroup extends Default {
        // UpdateGroup 继承自 Default，在校验 UpdateGroup 分组时
        // 也会自动包含所有没有指定分组的字段（默认分组）
    }
}
