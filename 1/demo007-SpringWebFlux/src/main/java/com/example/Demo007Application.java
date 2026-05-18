package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类
 *
 * 【为什么需要响应式编程？】
 *
 * 在传统的Servlet线程模型中，每个HTTP请求都会占用一个线程，直到请求处理完成才释放。
 * 这种模式在面对大量并发请求时会出现以下问题：
 *
 * 1. 线程资源耗尽：
 *    - 每个线程都占用一定的内存（通常1MB左右）
 *    - 线程的创建和销毁也有性能开销
 *    - 如果有10000个并发请求，就需要10000个线程
 *
 * 2. 阻塞导致的资源浪费：
 *    - 在数据库查询、远程API调用等IO操作时，线程处于等待状态
 *    - 这些等待时间完全可以用来处理其他请求
 *
 * 3. 扩展性问题：
 *    - 要支持更多并发，只能增加线程或机器
 *    - 成本高，效率低
 *
 * 【响应式编程的解决方案】
 *
 * 响应式编程是一种基于异步流和事件驱动的编程范式。
 * 核心思想是：
 * - 使用少量的线程来处理大量的并发请求
 * - 通过回调/订阅的方式处理异步操作的结果
 * - 利用事件循环（Event Loop）实现非阻塞IO
 *
 * Spring WebFlux就是Spring提供的响应式Web框架，它基于Project Reactor项目，
 * 提供了Mono（0或1个元素）和Flux（0或N个元素）两种响应式类型。
 *
 * 【Spring WebFlux vs Spring MVC】
 *
 * Spring MVC | Spring WebFlux
 * ---------- | -------------
 * 同步阻塞   | 异步非阻塞
 * Servlet API | 响应式流（Reactive Streams）
 * 每个请求一个线程 | 少量线程处理大量请求
 * 适用于业务复杂的CRUD | 适用于高并发IO密集型服务
 */
@SpringBootApplication
public class Demo007Application {

    public static void main(String[] args) {
        // 启动Spring Boot应用
        // SpringApplication.run()会启动嵌入式的Web服务器（Netty或Undertow）
        // 而不是传统的Tomcat，因为Tomcat是阻塞式的
        SpringApplication.run(Demo007Application.class, args);

        System.out.println("========================================");
        System.out.println("  Spring WebFlux 响应式编程示例已启动！  ");
        System.out.println("========================================");
        System.out.println();
        System.out.println("【核心概念说明】");
        System.out.println("1. Mono<T>  - 代表0或1个元素的异步序列，适用于单值响应");
        System.out.println("2. Flux<T>  - 代表0到N个元素的异步序列，适用于列表响应");
        System.out.println("3. 两种编程模型：@Controller注解式 和 RouterFunction函数式");
        System.out.println();
        System.out.println("【访问地址】");
        System.out.println("- 注解式控制器: http://localhost:8080/api/users");
        System.out.println("- 函数式端点:   http://localhost:8080/functional/users");
        System.out.println("- WebClient示例: http://localhost:8080/api/webclient/demo");
        System.out.println();
        System.out.println("【项目特点】");
        System.out.println("- 完全异步非阻塞");
        System.out.println("- 使用背压机制处理过快的数据流");
        System.out.println("- 支持响应式操作符（map, filter, flatMap, zip等）");
        System.out.println("========================================");
    }
}
