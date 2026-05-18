package com.example.model;

/**
 * User 实体类 - 演示 Spring 如何管理对象
 *
 * ============================================
 * 什么是实体类（Entity/Model）？
 * ============================================
 * 实体类是用于表示现实世界中"事物"的 Java 类。
 * 比如在一个用户管理系统中，User 就是表示"用户"这个概念的类。
 * 它包含了用户的相关属性（如姓名、年龄等）和基本行为。
 *
 * 在这个示例中，User 类非常简单，只有三个属性：
 * - id: 用户的唯一标识符
 * - name: 用户的名字
 * - email: 用户的邮箱
 *
 * ============================================
 * 这个类在 Spring 中的特殊意义
 * ============================================
 * 稍后在 MainApp.java 中，我们会看到两种创建 User 对象的方式：
 * 1. 传统方式：手动 new User() 创建
 * 2. Spring 方式：由 Spring IoC 容器创建
 *
 * 通过对比，你会明白为什么使用 Spring 可以让程序更加灵活和易于维护。
 */
public class User {

    // ============================================
    // 属性定义
    // ============================================

    /**
     * 用户的唯一标识符
     *
     * 为什么需要 id？
     * - 在程序中，我们需要唯一区分不同的用户
     * - 比如数据库中可能有多个叫"张三"的用户，但他们的 id 不同
     * - id 就是用来唯一标识每个用户的
     */
    private Long id;

    /**
     * 用户的姓名
     *
     * 注意：这里使用 String 类型而不是基本类型 String
     * - String 是引用类型，可以表示"没有设置姓名"的情况（null）
     * - 基本类型如 int 默认值是 0，但 0 可能是一个有效的姓名长度
     * - 所以使用 String 更灵活
     */
    private String name;

    /**
     * 用户的电子邮箱
     *
     * 为什么 email 也用 String？
     * - 邮箱地址本质上是字符串
     * - String 可以方便地进行各种文本处理
     */
    private String email;

    // ============================================
    // 构造方法
    // ============================================

    /**
     * 默认构造方法
     *
     * 什么是构造方法？
     * 构造方法是一种特殊的方法，在创建对象时自动调用。
     * - 构造方法的名字必须与类名相同
     * - 如果不定义构造方法，Java 会提供一个默认的无参构造方法
     * - 构造方法可以重载（定义多个参数不同的构造方法）
     *
     * 为什么这里要显式定义一个无参构造方法？
     * - 当我们定义了一个有参构造方法后，Java 不再自动提供无参构造方法
     * - 有些框架（如 Spring、MyBatis）可能需要无参构造方法来创建对象
     * - 所以保留无参构造方法是好的实践
     */
    public User() {
    }

    /**
     * 带参构造方法 - 方便快速创建 User 对象
     *
     * @param id    用户的唯一标识
     * @param name  用户的姓名
     * @param email 用户的邮箱
     *
     * 为什么需要带参构造方法？
     * - 当我们知道所有必要信息时，可以一步到位创建完整的 User 对象
     * - 这比先 new User() 再一个个 set 要方便得多
     * - 代码更简洁，也减少了出错的机会
     */
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // ============================================
    // Getter 和 Setter 方法
    // ============================================

    /**
     * 获取用户的唯一标识符
     *
     * 什么是 Getter 方法？
     * Getter 方法用于"读取"私有属性的值。
     * 我们将属性设为 private（私有），这样外部不能直接访问，
     * 必须通过我们提供的 Getter 方法来获取值。
     * 这是一种保护对象数据的方式，叫做"封装"。
     *
     * @return 用户的 id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户的唯一标识符
     *
     * 什么是 Setter 方法？
     * Setter 方法用于"修改"私有属性的值。
     * 通过 Setter 方法，我们可以控制如何修改属性，比如：
     * - 添加数据验证
     * - 记录修改日志
     * - 实现只读属性（只提供 Getter，不提供 Setter）
     *
     * @param id 新的用户 id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户的姓名
     * @return 用户的姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户的姓名
     * @param name 新的姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取用户的邮箱
     * @return 用户的邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置用户的邮箱
     * @param email 新的邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    // ============================================
    // toString 方法
    // ============================================

    /**
     * 重写 toString 方法，方便打印对象信息
     *
     * 什么是 toString？
     * toString 是 Object 类（所有 Java 类的父类）的一个方法。
     * 当我们使用 System.out.println(user) 打印对象时，会自动调用 toString。
     * 默认的 toString 返回的是对象的内存地址（如 User@1a2b3c4d），不太友好。
     * 我们重写这个方法，返回更有意义的信息。
     *
     * @return 用户的可读字符串表示
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
