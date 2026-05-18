package com.example.router;

import com.example.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

/**
 * 用户路由配置（函数式编程模型）
 *
 * 【什么是RouterFunction？】
 *
 * RouterFunction是Spring WebFlux函数式编程模型中的路由组件。
 * 它负责将HTTP请求映射到对应的HandlerFunction。
 *
 * 【RouterFunction vs @RequestMapping】
 *
 * | 特性           | RouterFunction              | @RequestMapping               |
 * |---------------|-----------------------------|-------------------------------|
 * | 配置方式        | Java代码（函数式）           | 注解（声明式）                 |
 * | 路由逻辑        | 集中在一个或多个Router中      | 分散在各个Controller中         |
 * | 灵活性         | 高 - 可以动态生成路由         | 中 - 静态配置                  |
 * | 代码量         | 较多                         | 较少                          |
 * | 调试           | 较难                         | 较容易                        |
 *
 * 【RouterFunction的结构】
 *
 * RouterFunction<ServerResponse>的结构：
 * - route(Predicate<ServerRequest>, HandlerFunction<ServerResponse>)
 * - 返回一个RouterFunction对象
 *
 * 常用Predicate：
 * - GET("/path") - 匹配GET请求
 * - POST("/path") - 匹配POST请求
 * - PUT("/path") - 匹配PUT请求
 * - DELETE("/path") - 匹配DELETE请求
 * - path("/path") - 路径前缀匹配
 * - accept(MediaType) - Content-Type匹配
 *
 * 【路由匹配顺序】
 *
 * 路由按照"先匹配先处理"的原则：
 * 1. 第一个匹配的路由被使用
 * 2. 其他路由不再被尝试
 * 3. 因此，更具体的路由应该放在前面
 *
 * 【路由嵌套】
 *
 * 可以使用nest()方法实现路由嵌套：
 * - 将共同的路径前缀提取出来
 * - 减少重复代码
 *
 * 示例：
 * router = nest(path("/api"),
 *     route(GET("/users"), handler::getUsers)
 *     .andRoute(GET("/posts"), handler::getPosts)
 * );
 *
 * 等价于：
 * router = route(GET("/api/users"), handler::getUsers)
 *           .and(route(GET("/api/posts"), handler::getPosts));
 */
@Configuration
public class UserRouter {

    /**
     * 创建路由Bean
     *
     * @Bean注解告诉Spring这是一个Bean
     * Spring会自动调用这个方法，并将返回的RouterFunction注册到上下文中
     *
     * @param userHandler 用户处理器（自动注入）
     * @return 路由函数
     */
    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        // 使用RouterFunctions.route()创建路由
        // 第一个参数是请求谓词（匹配条件）
        // 第二个参数是处理器函数（处理逻辑）

        return route()

                // ============ 用户相关路由 ============

                // GET /functional/users - 获取所有用户
                // 使用GET方法，路径为/functional/users
                .GET("/functional/users",
                        accept(APPLICATION_JSON),
                        userHandler::getAllUsers)
                // accept(APPLICATION_JSON)确保只匹配JSON请求

                // GET /functional/users/{id} - 根据ID获取用户
                // {id}是路径变量，会被提取出来传给Handler
                .GET("/functional/users/{id}",
                        accept(APPLICATION_JSON),
                        userHandler::getUserById)

                // POST /functional/users - 创建用户
                // contentType指定接受的请求内容类型
                .POST("/functional/users",
                        contentType(APPLICATION_JSON),
                        userHandler::createUser)

                // PUT /functional/users/{id} - 更新用户
                .PUT("/functional/users/{id}",
                        contentType(APPLICATION_JSON),
                        userHandler::updateUser)

                // DELETE /functional/users/{id} - 删除用户
                .DELETE("/functional/users/{id}",
                        userHandler::deleteUser)

                // ============ 高级路由示例 ============

                // GET /functional/users/search - 搜索用户
                // 通过查询参数?name=xxx&limit=10进行搜索
                .GET("/functional/users/search",
                        accept(APPLICATION_JSON),
                        userHandler::searchUsers)

                // POST /functional/users/process - 批量处理用户
                .POST("/functional/users/process",
                        contentType(APPLICATION_JSON),
                        userHandler::processUsers)

                // 构建路由函数
                .build();
    }

    /**
     * 路由嵌套示例
     *
     * 这个方法演示了如何使用nest()方法组织嵌套路由
     * 在实际项目中，可以将相关功能的路由组织在一起
     */
    @Bean
    public RouterFunction<ServerResponse> nestedUserRoutes(UserHandler userHandler) {
        // 使用nest方法创建嵌套路由
        // 所有以/functional/admin开头的请求都会在这里匹配
        return nest(path("/functional/admin"),
                route(GET("/users"), userHandler::getAllUsers)
                        .andRoute(GET("/users/{id}"), userHandler::getUserById)
        );
    }

    /**
     * 条件路由示例
     *
     * 这个方法演示了如何使用条件谓词创建动态路由
     */
    @Bean
    public RouterFunction<ServerResponse> conditionalRoutes(UserHandler userHandler) {
        return route()
                // 根据查询参数决定路由
                // /functional/conditional?mode=detailed 返回详细信息
                .GET("/functional/conditional",
                        request -> {
                            String mode = request.queryParam("mode").orElse("normal");
                            if ("detailed".equals(mode)) {
                                return userHandler.getAllUsers(request);
                            } else {
                                return ServerResponse.ok()
                                        .bodyValue("Normal mode");
                            }
                        })

                .build();
    }
}

/**
 * 【RouterFunction的高级用法】
 *
 * 1. 路由组合
 *    - route().and(route()) - 组合多个路由
 *    - 或使用andRoute()便捷方法
 *
 * 2. 路由前缀
 *    - nest(path("/api"), routes) - 为一组路由添加前缀
 *
 * 3. 过滤器
 *    - .filter((request, next) -> {...}) - 添加过滤器
 *    - 可以用于日志、认证、监控等
 *
 * 4. 谓词组合
 *    - predicate.and(predicate) - 同时满足
 *    - predicate.or(predicate) - 满足其一
 *    - predicate.negate() - 取反
 *
 * 【最佳实践】
 *
 * 1. 路由定义应该清晰、有序
 * 2. 优先使用具体路径，再使用通配符
 * 3. 相关路由应该放在一起
 * 4. 复杂逻辑应该封装到Handler中
 * 5. 路由配置应该易于测试
 */
