/*
 * =====================================================
 * 依赖注入示例：消费者类
 * =====================================================
 *
 * 【演示两种注入方式】
 *
 * 这个类演示了Spring支持的主要依赖注入方式：
 * 1. 构造器注入（Constructor Injection）
 * 2. Setter注入（Setter Injection / Method Injection）
 *
 * 【构造器注入 vs Setter注入 详细对比】
 *
 * ┌─────────────────┬────────────────────────┬────────────────────────┐
 * │     特性        │      构造器注入         │      Setter注入        │
 * ├─────────────────┼────────────────────────┼────────────────────────┤
 * │ 初始化时机      │ 创建时完成初始化        │ 创建后可逐步初始化      │
 * │ 依赖数量        │ 适合固定依赖           │ 适合大量或可选依赖      │
 * │ 可变性          │ 不可变                │ 可变                   │
 * │ 测试友好度      │ 非常友好              │ 较友好                 │
 * │ 空值处理        │ 可在构造时检查         │ 可在setter中检查        │
 * │ 循环依赖        │ 可能导致循环依赖       │ 可以处理循环依赖        │
 * └─────────────────┴────────────────────────┴────────────────────────┘
 *
 * 【推荐实践】
 *
 * 1. 必需依赖使用构造器注入
 * 2. 可选依赖使用Setter注入
 * 3. 也可以同时使用两种方式
 *
 * =====================================================
 */
package com.example.di;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消费者类
 *
 * 演示如何使用注入的消息服务。
 * 消费者本身不创建消息服务，而是由Spring IoC容器注入。
 *
 * 【依赖注入的好处】
 *
 * 1. 松耦合：Consumer不直接创建MessageService
 * 2. 可测试：可以注入mock的MessageService进行测试
 * 3. 可替换：可以切换不同的MessageService实现
 * 4. 清晰：依赖关系通过构造方法和setter明确表达
 */
public class Consumer {

    // ============================================
    // 构造器注入的依赖
    // ============================================

    /**
     * 消息服务实例（通过构造器注入）
     *
     * 【final关键字】
     * 使用final修饰，表明这个引用在构造后不可改变。
     * 这是构造器注入的一个重要优点：依赖不可变。
     * 注意：当使用无参构造器时，此字段不会被初始化
     */
    private MessageService constructorInjectedService;

    // ============================================
    // Setter注入的依赖
    // ============================================

    /**
     * 消息服务实例（通过Setter注入）
     *
     * 这个引用不是final，可以在对象创建后通过setter修改。
     * 这是Setter注入的特点：依赖可变。
     */
    private MessageService setterInjectedService;

    // ============================================
    // 无参构造器（用于Setter注入演示）
    // ============================================

    /**
     * 无参构造方法
     *
     * 【为什么需要无参构造器】
     * 当使用Setter注入时，对象先通过无参构造器创建，
     * 然后再通过setter方法注入依赖。
     * 如果没有无参构造器，Spring无法创建对象进行Setter注入。
     */
    public Consumer() {
        System.out.println("【Consumer】无参构造器被调用（用于Setter注入）");
    }

    // ============================================
    // 构造器注入演示
    // ============================================

    /**
     * 构造方法 - 演示构造器注入
     *
     * 【构造器注入的实现】
     * Spring会自动调用这个构造方法，并将符合条件的MessageService bean传入。
     * 这里使用接口类型，Spring会自动注入EmailService或SmsService的实现。
     *
     * 【@Autowired的作用】
     * 当只有一个构造方法时，@Autowired可以省略。
     * 但如果有多个构造方法，需要用@Autowired明确指定使用哪个。
     *
     * 【参数解析】
     * - 参数类型：MessageService（接口）
     * - Spring会找到该接口的唯一实现类进行注入
     * - 如果有多个实现类，需要使用@Primary或@Qualifier指定
     *
     * @param service 通过构造器注入的消息服务
     */
    @Autowired
    public Consumer(MessageService service) {
        this.constructorInjectedService = service;
        /*
         * 注意：这里的参数名称"service"不会影响bean的匹配。
         * Spring匹配bean是根据类型（MessageService），而不是参数名称。
         * 参数名称在编译时可能被保留（-parameters编译选项），但这不是主要匹配依据。
         */
    }

    // ============================================
    // Setter注入演示
    // ============================================

    /**
     * Setter方法 - 演示Setter注入
     *
     * 【Setter注入的实现】
     * 使用@Autowired标注setter方法，Spring会自动调用这个方法，
     * 并传入MessageService类型的bean。
     *
     * 【与构造器注入的区别】
     * - 构造器注入：依赖在对象创建时一次性全部注入
     * - Setter注入：对象先创建，然后逐步通过setter注入依赖
     *
     * 【可选依赖】
     * 如果某个依赖是可选的，可以使用setter注入，配合@Nullable注解：
     * public void setService(@Nullable MessageService service)
     *
     * @param service 通过setter注入的消息服务
     */
    @Autowired
    public void setService(MessageService service) {
        this.setterInjectedService = service;
        System.out.println("【Setter注入】MessageService已被注入: " + service.getClass().getSimpleName());
    }

    // ============================================
    // 业务方法
    // ============================================

    /**
     * 处理消息的方法
     *
     * 演示使用构造器注入的服务
     *
     * @param message 要处理的消息内容
     * @param recipient 接收者
     */
    public void processMessage(String message, String recipient) {
        System.out.println("\n>>> Consumer开始处理消息...");
        System.out.println(">>> 使用的服务（构造器注入）: " + constructorInjectedService.getClass().getSimpleName());
        constructorInjectedService.sendMessage(message, recipient);
        System.out.println(">>> 消息处理完成！");
    }

    /**
     * 处理消息的方法（使用Setter注入的服务）
     *
     * 演示使用setter注入的服务
     *
     * @param message 要处理的消息内容
     * @param recipient 接收者
     */
    public void processMessageWithSetter(String message, String recipient) {
        System.out.println("\n>>> Consumer开始处理消息（Setter注入）...");
        System.out.println(">>> 使用的服务: " + (setterInjectedService != null ? setterInjectedService.getClass().getSimpleName() : "null"));
        if (setterInjectedService != null) {
            setterInjectedService.sendMessage(message, recipient);
        } else {
            System.out.println(">>> 警告：Setter注入的服务尚未设置！");
        }
        System.out.println(">>> 消息处理完成！");
    }

    /**
     * 获取注入的服务信息（用于调试）
     *
     * @return 构造器注入的服务类名
     */
    public String getConstructorInjectedServiceInfo() {
        return constructorInjectedService.getClass().getName();
    }

    /**
     * 获取注入的服务信息（用于调试）
     *
     * @return Setter注入的服务类名，如果未注入则返回null
     */
    public String getSetterInjectedServiceInfo() {
        return setterInjectedService != null ? setterInjectedService.getClass().getName() : null;
    }
}
