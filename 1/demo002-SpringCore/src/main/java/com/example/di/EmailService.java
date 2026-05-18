/*
 * =====================================================
 * 依赖注入示例：邮件服务实现类
 * =====================================================
 *
 * 【演示构造器注入】
 *
 * 这个类实现了MessageService接口，使用构造器注入方式。
 * 构造器注入是Spring推荐的主要注入方式。
 *
 * 【构造器注入的特点】
 *
 * 1. 优点：
     * - 依赖不可变：构造后无法修改
     * - 强制必需依赖：没有可选依赖的问题
     * - 完全初始化：对象创建时所有依赖都已准备好
     * - 易于测试：构造时直接传入mock依赖
     * - 清晰：阅读构造方法即可知道类的所有必需依赖
     *
 * 2. 缺点：
     * - 如果依赖过多，构造方法参数会很多
     * - 不适合可选依赖
     *
 * 【@Component vs @Bean】
 *
 * @Component用于自动扫描和自动装配，
 * @Bean用于在Java配置类中显式声明bean。
 * 这里使用@Bean是为了演示配置类的用法。
 *
 * =====================================================
 */
package com.example.di;

import org.springframework.stereotype.Component;

/**
 * 邮件服务实现类
 *
 * 演示通过电子邮件发送消息的功能。
 * 这个类的实例会被Spring IoC容器管理，
 * 并通过构造器注入的方式提供给Consumer使用。
 */
public class EmailService implements MessageService {

    /**
     * 发送邮件的实现
     *
     * 【这里省略了真实的邮件发送逻辑】
     * 实际生产中，这里会使用JavaMailSender或第三方库发送邮件
     *
     * @param message 邮件内容
     * @param recipient 收件人邮箱地址
     * @return 发送是否成功
     */
    @Override
    public boolean sendMessage(String message, String recipient) {
        // 模拟邮件发送过程
        System.out.println("【EmailService】准备发送邮件...");
        System.out.println("【EmailService】收件人: " + recipient);
        System.out.println("【EmailService】内容: " + message);
        System.out.println("【EmailService】邮件发送成功！");

        /*
         * 实际生产中的邮件发送可能会涉及：
         * - SMTP服务器配置
         * - 邮件模板
         * - 附件处理
         * - 发送状态跟踪
         * - 失败重试
         * 等等，这些都可以通过Spring的邮件扩展来支持
         */

        return true;
    }
}
