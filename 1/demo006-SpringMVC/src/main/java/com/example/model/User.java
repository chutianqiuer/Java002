package com.example.model;

import java.io.Serializable;

/**
 * 用户实体类
 *
 * 本类作为Spring MVC示例中的模型对象使用。
 * 用于演示数据绑定、表单处理、JSON序列化等功能。
 *
 * Spring MVC的请求处理流程：
 * 1. 用户发送请求到服务器
 * 2. DispatcherServlet接收请求
 * 3. HandlerMapping根据URL找到对应的Controller方法
 * 4. Controller处理业务逻辑，返回ModelAndView
 * 5. ViewResolver解析视图名，找到对应的JSP
 * 6. JSP渲染Model中的数据，生成响应
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（实际项目中应加密存储）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 默认构造函数
     * Spring MVC需要无参构造函数来实例化对象
     */
    public User() {
    }

    /**
     * 带参构造函数
     * @param id 用户ID
     * @param username 用户名
     * @param password 密码
     */
    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * 全参构造函数
     */
    public User(Long id, String username, String password, String email, Integer age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }

    // ==================== Getter和Setter方法 ====================

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取邮箱
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取年龄
     * @return 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
