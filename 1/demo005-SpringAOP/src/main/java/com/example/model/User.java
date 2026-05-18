package com.example.model;

/**
 * 用户模型类
 *
 * AOP概念解析：
 * - Join Point（连接点）：Spring AOP中，连接点是程序执行的某个具体位置，
 *   比如方法调用处、方法内部等。在这个类中，每个字段的访问都可以是连接点。
 *   但更常见的是，UserService中的每个方法调用都是一个连接点。
 *
 * - Target（目标对象）：被AOP代理增强的对象。在我们的例子中，
 *   UserService就是目标对象，切面会拦截其中的方法。
 *
 * 本类作为数据模型，用于在业务方法之间传递数据。
 */
public class User {

    /**
     * 用户ID - 唯一标识一个用户
     */
    private Long id;

    /**
     * 用户名 - 用于登录和显示
     */
    private String username;

    /**
     * 邮箱 - 用户的电子邮件地址
     */
    private String email;

    /**
     * 构造函数 - 创建用户对象
     * @param id 用户ID
     * @param username 用户名
     * @param email 邮箱
     */
    public User(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // ========== Getter和Setter方法 ==========

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 重写toString方法，便于打印用户信息
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
