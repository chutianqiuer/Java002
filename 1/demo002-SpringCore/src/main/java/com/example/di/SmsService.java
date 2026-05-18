/*
 * =====================================================
 * 依赖注入示例：短信服务实现类
 * =====================================================
 *
 * 【演示Setter注入】
 *
 * 这个类实现了MessageService接口，使用setter注入方式。
 * Setter注入允许依赖在对象创建后动态设置。
 *
 * 【Setter注入的特点】
 *
 * 1. 优点：
     * - 可选依赖：某些依赖可以不设置，有默认值
     * - 灵活：可以在对象创建后动态更改依赖
     * - 重构友好：如果需要新增可选依赖，不影响现有构造方法
     * - 部分初始化：创建对象后可以逐步设置依赖
     *
     * 2. 缺点：
     * - 依赖可变：setter后可以改变，有被意外修改的风险
     * - 可能出现半初始化状态：对象创建时依赖可能还未设置
     * - 不够明确：需要阅读更多代码才能知道有哪些依赖
     *
 * 【构造器注入 vs Setter注入】
 *
 * 最佳实践：
 * - 必需依赖：使用构造器注入
 * - 可选依赖：使用Setter注入
 * - Spring团队推荐：尽可能使用构造器注入
 *
 * =====================================================
 */
package com.example.di;

/**
 * 短信服务实现类
 *
 * 演示通过短信发送消息的功能。
 * 这个类的实例会被Spring IoC容器管理，
 * 并通过setter注入的方式提供给Consumer使用。
 */
public class SmsService implements MessageService {

    /**
     * 发送短信的实现
     *
     * 【这里省略了真实的短信发送逻辑】
     * 实际生产中，这里会使用短信网关API发送短信
     *
     * @param message 短信内容
     * @param recipient 收件人手机号码
     * @return 发送是否成功
     */
    @Override
    public boolean sendMessage(String message, String recipient) {
        // 模拟短信发送过程
        System.out.println("【SmsService】准备发送短信...");
        System.out.println("【SmsService】收件人手机: " + recipient);
        System.out.println("【SmsService】内容: " + message);
        System.out.println("【SmsService】短信发送成功！");

        /*
         * 实际生产中的短信发送可能会涉及：
         * - 短信网关选择（阿里云、腾讯云等）
         * - 短信签名和模板
         * - 发送频率限制
         * - 发送状态回执
         * - 费用统计
         * 等等
         */

        return true;
    }
}
