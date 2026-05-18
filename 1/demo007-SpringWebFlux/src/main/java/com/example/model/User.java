package com.example.model;

/**
 * 用户实体类
 *
 * 【响应式编程中的数据传输】
 *
 * 在响应式编程中，数据以"流"的形式传递，而不是一次性加载到内存。
 * 这里的User对象就是流中的数据类型。
 *
 * 【响应式模型与普通Java对象的区别】
 * - 响应式模型（如User）本身可以是普通的POJO
 * - 关键是它被Mono或Flux包装后，才能享受响应式的好处
 * - 例如：Flux<User>表示一个异步的用户列表流
 *
 * 【设计原则】
 * - 使用不可变对象（所有字段final）
 * - 提供完整的构造函数
 * - 重写toString()便于日志输出
 */
public class User {

    /**
     * 用户ID - 使用Long类型支持更大范围
     */
    private final Long id;

    /**
     * 用户名
     */
    private final String name;

    /**
     * 用户邮箱
     */
    private final String email;

    /**
     * 用户年龄
     */
    private final Integer age;

    /**
     * 全参数构造函数
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

    // ==================== Getter方法 ====================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    /**
     * 便捷的静态工厂方法 - 创建用户
     * 使用Builder模式可以更优雅地创建对象
     */
    public static User create(Long id, String name, String email, Integer age) {
        return new User(id, name, email, age);
    }

    /**
     * 重写toString方法，便于调试和日志输出
     */
    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', age=%d}",
                id, name, email, age);
    }

    /**
     * 链式调用的Builder模式内部类
     * 使用示例：User.builder().id(1L).name("张三").email("zhangsan@example.com").age(25).build()
     */
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String name;
        private String email;
        private Integer age;

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder age(Integer age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(id, name, email, age);
        }
    }
}
