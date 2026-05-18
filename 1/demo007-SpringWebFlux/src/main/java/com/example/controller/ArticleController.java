package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章控制器（用于演示RouterFunction和WebClient）
 *
 * 【RouterFunction vs @Controller】
 *
 * 虽然这个类使用的是@RestController注解，
 * 但它的路由是通过UserRouter中的RouterFunction配置的，
 * 专门用于演示函数式路由的使用方式。
 *
 * 【本控制器演示的内容】
 *
 * 1. 简单的REST API（注解式）
 * 2. 响应式数据返回
 * 3. 模拟数据处理
 *
 * 【RouterFunction中的路由定义】
 *
 * 在UserRouter中定义了以下文章相关路由：
 * - GET /functional/articles - 获取所有文章
 * - GET /functional/articles/{id} - 获取单篇文章
 *
 * 注意：这些路由虽然使用@RestController的端点，
 * 但在RouterFunction中配置路由是可能的。
 * 不过通常情况下，@RestController的端点通过注解自动映射，
 * 而RouterFunction用于定义那些不能用注解简单表示的路由。
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    /**
     * 模拟的文章数据
     */
    private static final Map<Long, Map<String, Object>> MOCK_ARTICLES = new HashMap<>();

    static {
        // 初始化模拟文章数据
        Map<String, Object> article1 = new HashMap<>();
        article1.put("id", 1L);
        article1.put("title", "Spring WebFlux 入门指南");
        article1.put("content", "本文介绍Spring WebFlux响应式编程框架的基本概念和使用方法。");
        article1.put("author", "张三");
        article1.put("views", 1000);
        MOCK_ARTICLES.put(1L, article1);

        Map<String, Object> article2 = new HashMap<>();
        article2.put("id", 2L);
        article2.put("title", "响应式编程核心概念");
        article2.put("content", "深入理解Mono、Flux以及响应式操作符。");
        article2.put("author", "李四");
        article2.put("views", 2000);
        MOCK_ARTICLES.put(2L, article2);

        Map<String, Object> article3 = new HashMap<>();
        article3.put("id", 3L);
        article3.put("title", "WebClient最佳实践");
        article3.put("content", "如何使用WebClient进行非阻塞式HTTP调用。");
        article3.put("author", "王五");
        article3.put("views", 1500);
        MOCK_ARTICLES.put(3L, article3);
    }

    /**
     * 获取所有文章
     *
     * 【响应式Map】
     *
     * MOCK_ARTICLES.entrySet()是一个普通的Set
     * Flux.fromIterable()将其转换为Flux
     * map()将每个Map.Entry转换为Map<String, Object>
     *
     * 【模拟延迟】
     *
     * delayElements()模拟从数据库或远程服务获取数据的延迟
     * 这有助于观察响应式流的工作方式
     *
     * @return 文章列表
     */
    @GetMapping
    public reactor.core.publisher.Flux<Map<String, Object>> getAllArticles() {
        return reactor.core.publisher.Flux.fromIterable(MOCK_ARTICLES.values())
                .delayElements(Duration.ofMillis(200))  // 模拟延迟
                .log("ArticleController.getAllArticles");
    }

    /**
     * 根据ID获取文章
     *
     * 【Mono处理】
     *
     * findById返回Mono：
     * - 找到文章：Mono.just(article)
     * - 没找到：Mono.empty()
     *
     * @param id 文章ID
     * @return 文章Mono
     */
    @GetMapping("/{id}")
    public Mono<Map<String, Object>> getArticleById(@PathVariable Long id) {
        Map<String, Object> article = MOCK_ARTICLES.get(id);
        return Mono.justOrEmpty(article)
                .delayElement(Duration.ofMillis(100))
                .log("ArticleController.getArticleById");
    }

    /**
     * 获取文章统计信息
     *
     * 【聚合操作】
     *
     * 这个端点演示了如何聚合计数：
     * - 先计算文章总数
     * - 再计算总浏览量
     * - 最后组合成统计数据
     *
     * 【zip操作符】
     *
     * zip()用于组合多个数据流：
     * - 等待所有输入流都有数据
     * - 组合成一个元组或新对象
     *
     * @return 统计信息
     */
    @GetMapping("/stats")
    public Mono<Map<String, Object>> getArticleStats() {
        Mono<Long> totalArticles = Mono.just((long) MOCK_ARTICLES.size());

        Mono<Long> totalViews = Mono.just(
                MOCK_ARTICLES.values().stream()
                        .mapToLong(article -> ((Number) article.get("views")).longValue())
                        .sum()
        );

        // 使用zip组合两个Mono
        return Mono.zip(totalArticles, totalViews)
                .map(tuple -> {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("totalArticles", tuple.getT1());
                    stats.put("totalViews", tuple.getT2());
                    stats.put("averageViews", tuple.getT2() / tuple.getT1());
                    stats.put("timestamp", LocalDateTime.now().toString());
                    return stats;
                })
                .log("ArticleController.getArticleStats");
    }

    /**
     * 搜索文章
     *
     * 【filter操作符】
     *
     * 根据标题关键字搜索文章：
     * - filter()过滤满足条件的文章
     * - map()转换输出格式
     *
     * @param keyword 搜索关键字
     * @return 匹配的文章
     */
    @GetMapping("/search")
    public reactor.core.publisher.Flux<Map<String, Object>> searchArticles(
            @PathVariable(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            keyword = "";
        }

        final String searchKey = keyword.toLowerCase();

        return reactor.core.publisher.Flux.fromIterable(MOCK_ARTICLES.values())
                .filter(article -> {
                    String title = (String) article.get("title");
                    return title.toLowerCase().contains(searchKey);
                })
                .map(article -> {
                    // 添加搜索高亮标记
                    Map<String, Object> result = new HashMap<>(article);
                    result.put("highlighted", true);
                    result.put("searchTime", LocalDateTime.now().toString());
                    return result;
                })
                .log("ArticleController.searchArticles");
    }
}

/**
 * 【WebClient示例说明】
 *
 * WebClient是Spring WebFlux提供的非阻塞式HTTP客户端，
 * 用于替代RestTemplate进行HTTP调用。
 *
 * 特点：
 * 1. 完全异步非阻塞
 * 2. 支持响应式流（Flux/Mono）
 * 3. 内置背压支持
 * 4. 函数式API
 *
 * 使用示例：
 *
 * WebClient webClient = WebClient.create();
 *
 * // GET请求
 * Mono<String> result = webClient.get()
 *     .uri("https://api.example.com/user/{id}", 1)
 *     .retrieve()
 *     .bodyToMono(String.class);
 *
 * // POST请求
 * Mono<User> result = webClient.post()
 *     .uri("https://api.example.com/users")
 *     .bodyValue(user)
 *     .retrieve()
 *     .bodyToMono(User.class);
 *
 * // 处理响应
 * result.subscribe(
 *     data -> System.out.println("Received: " + data),
 *     error -> System.err.println("Error: " + error),
 *     () -> System.out.println("Completed")
 * );
 *
 * 【背压（Backpressure）】
 *
 * 背压是响应式编程中的重要概念：
 *
 * 想象一个场景：
 * - 数据源（数据库）每秒产生10000条数据
 * - 消费者只能每秒处理1000条
 *
 * 没有背压：消费者会被淹没，导致内存溢出
 * 有背压：消费者告诉生产者"我只想要1000条"，生产者据此调整
 *
 * Reactor的背压机制：
 * - request(n) 方法告诉发布者想要多少数据
 * - 发布者最多发送n个数据
 * - 消费者可以动态调整请求数量
 *
 * 示例：
 * flux.take(10)  // 只取前10个
 * flux.buffer(100)  // 缓冲100个再处理
 */
