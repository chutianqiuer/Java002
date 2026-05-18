package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 用户实体类
 *
 * 【为什么要实现Serializable接口？】
 * 因为缓存数据可能会被存储到磁盘或通过网络传输，
 * 实现Serializable接口可以保证对象可以被序列化/反序列化。
 * 对于进程内缓存（如ConcurrentHashMap、Caffeine），这个接口不是强制的；
 * 但如果切换到Redis等分布式缓存，就必须实现Serializable。
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID - 用作缓存的key
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户年龄
     */
    private Integer age;

    /**
     * 默认构造函数
     * 【注意】：JPA/Hibernate等框架需要无参构造函数
     */
    public User() {
    }

    /**
     * 带参数的构造函数
     *
     * @param id    用户ID
     * @param name  用户名
     * @param email 用户邮箱
     * @param age   用户年龄
     */
    public User(Long id, String name, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // ==================== Getter和Setter方法 ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 重写toString方法，便于日志输出和调试
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }

    /**
     * 重写equals方法，用于比较两个User对象是否相等
     * 【缓存key比较需要用到】
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * 重写hashCode方法，与equals方法保持一致
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
