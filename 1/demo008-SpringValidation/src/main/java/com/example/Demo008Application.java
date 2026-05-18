package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Validation 示例项目启动类
 *
 * 本项目演示 Spring Framework 中的数据校验功能，这是企业级应用开发中非常重要的部分。
 *
 * 什么是 Spring Validation？
 * Spring Validation 是 Spring Framework 提供的数据校验框架，基于 JSR-303/JSR-380（Bean Validation）规范。
 * 它允许开发者在实体类上使用注解来声明校验规则，然后在 Controller 层自动触发校验，
 * 无需在业务代码中编写大量的 if-else 校验逻辑。
 *
 * 主要特性：
 * 1. 声明式校验：通过注解定义校验规则，代码简洁清晰
 * 2. 嵌套校验：支持对象嵌套和集合元素的递归校验
 * 3. 分组校验：可以根据不同场景选择性地执行校验规则
 * 4. 自定义校验：支持编写自定义校验器来满足业务特定需求
 * 5. 国际化错误消息：校验失败时的错误信息支持国际化配置
 * 6. 与 Spring MVC 集成：在 Controller 层自动触发校验并处理错误
 *
 * JSR-303 和 JSR-380 区别：
 * - JSR-303 (Bean Validation 1.0)：JavaEE 6 提出，支持基本校验注解如 @NotNull, @Size 等
 * - JSR-380 (Bean Validation 2.0)：JavaEE 7/8 提出，增加了 @Email, @Past, @Future, @Positive 等注解
 */
@SpringBootApplication
public class Demo008Application {

    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(Demo008Application.class, args);

        // 启动成功后打印提示信息
        System.out.println("========================================");
        System.out.println("Spring Validation 示例项目已启动成功！");
        System.out.println("========================================");
        System.out.println("访问地址：http://localhost:8080");
        System.out.println("API 端点：");
        System.out.println("  POST /api/user/register - 用户注册（综合校验演示）");
        System.out.println("  POST /api/user/create - 创建用户（分组校验演示）");
        System.out.println("  POST /api/user/update - 更新用户（分组校验演示）");
        System.out.println("  GET  /api/user/{id} - 获取用户（简单参数校验）");
        System.out.println("========================================");
    }
}
