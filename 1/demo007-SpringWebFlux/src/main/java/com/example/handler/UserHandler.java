package com.example.handler;

import com.example.model.User;
import com.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户处理器（函数式编程模型）
 *
 * 【什么是Handler？】
 *
 * Handler是Spring WebFlux函数式编程模型中的核心组件之一。
 * 它相当于传统MVC中的@Service层，负责处理具体的业务逻辑。
 *
 * 【Handler vs Controller】
 *
 * | 特性         | HandlerFunction              | @Controller/@Service        |
 * |--------------|------------------------------|------------------------------|
 * | 返回类型      | Mono<ServerResponse>        | @ResponseBody / Mono<?>      |
 * | 参数类型      | ServerRequest               | @PathVariable, @RequestBody  |
 * | 编程风格      | 函数式                       | 注解式                       |
 * | 代码组织      | 分散在多个Handler中          | 集中在Controller中           |
 * | 学习曲线      | 较陡                         | 较平缓                       |
 *
 * 【为什么需要Handler？】
 *
 * 在函数式编程模型中，我们将路由配置和业务逻辑分离：
 * - Router：负责URL到Handler的映射
 * - Handler：负责具体的业务逻辑
 *
 * 这种分离的好处：
 * 1. 路由规则集中管理
 * 2. 业务逻辑独立测试
 * 3. 更容易实现动态路由
 * 4. 更灵活的组合式路由
 *
 * 【Handler的工作流程】
 *
 * 请求 -> Router匹配 -> Handler处理 -> ServerResponse返回
 *              |
 *              v
 *    ServerRequest包含：
 *    - pathVariables() - 路径变量
 *    - bodyToMono() - 请求体
 *    - queryParam() - 查询参数
 *
 *    Handler返回：
 *    - ServerResponse.ok().body() - 200 OK + 响应体
 *    - ServerResponse.notFound().build() - 404
 *    - ServerResponse.badRequest().build() - 400
 */
public class UserHandler {

    /**
     * 用户服务（响应式服务）
     */
    private final UserService userService;

    /**
     * 构造函数注入
     */
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取所有用户
     *
     * 【Handler函数的返回类型】
     *
     * 所有Handler函数都返回Mono<ServerResponse>：
     * - Mono<ServerResponse>是一个异步的HTTP响应
     * - ServerResponse是不可变的响应对象
     * - 可以设置状态码、响应头、响应体
     *
     * 【响应构建流程】
     * 1. ServerResponse.ok() - 创建200 OK响应
     * 2. .contentType() - 设置内容类型
     * 3. .body() - 设置响应体（可以是Mono或Flux）
     * 4. .build() - 构建响应对象
     *
     * @param request HTTP请求
     * @return 所有用户的响应
     */
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        // 调用服务获取所有用户
        Flux<User> users = userService.getAllUsers();

        // 构建200 OK响应，响应体是用户列表
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(users, User.class)
                .log("Handler.getAllUsers");
    }

    /**
     * 根据ID获取用户
     *
     * 【从请求中提取路径变量】
     *
     * ServerRequest.pathVariables()返回Map<String, String>：
     * - 路径变量以String形式返回
     * - 需要手动转换类型
     * - 建议添加类型转换失败的处理
     *
     * 【处理不存在的资源】
     *
     * 响应式编程中，"找不到"不是异常，而是空结果。
     * 常见处理方式：
     * 1. switchIfEmpty() - 切换到备用响应
     * 2. flatMap() + 条件判断 - 检查是否有值
     * 3. hasElement() - 检查元素是否存在
     *
     * @param request HTTP请求
     * @return 用户或404响应
     */
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        // 从路径中获取ID
        String idStr = request.pathVariable("id");
        Long id;

        // 类型转换，并处理无效输入
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return ServerResponse.badRequest()
                    .bodyValue(createError("无效的用户ID: " + idStr));
        }

        // 调用服务获取用户
        Mono<User> userMono = userService.getUserById(id);

        // 使用Mono的扁平化处理：
        // 如果找到用户，返回200和用户
        // 如果没找到用户，返回404
        return userMono
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build())
                .log("Handler.getUserById");
    }

    /**
     * 创建用户
     *
     * 【从请求中提取请求体】
     *
     * ServerRequest.bodyToMono()用于提取请求体：
     * - bodyToMono(User.class) - 将JSON反序列化为User对象
     * - bodyToFlux(User.class) - 将JSON数组反序列化为Flux<User>
     *
     * 【POST请求的响应码】
     *
     * 创建资源成功后，通常返回：
     * - 201 Created - 标准做法
     * - 200 OK - 也常用
     * - 可以包含Location头指向新创建的资源
     *
     * @param request HTTP请求
     * @return 创建的用户
     */
    public Mono<ServerResponse> createUser(ServerRequest request) {
        // 从请求体中提取用户对象
        Mono<User> userMono = request.bodyToMono(User.class);

        // 使用flatMap处理异步创建操作
        return userMono
                .flatMap(user -> {
                    // 调用服务创建用户
                    Mono<User> createdUser = userService.createUser(user);
                    return createdUser;
                })
                .flatMap(user -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .log("Handler.createUser");
    }

    /**
     * 更新用户
     *
     * 【PUT请求的处理】
     *
     * PUT是幂等更新，通常返回：
     * - 200 OK + 更新后的资源
     * - 404 Not Found（如果资源不存在）
     *
     * 【响应式错误处理】
     *
     * 可以在Handler层进行统一的错误处理：
     * - onErrorResume() - 错误时返回默认值或替代响应
     * - onErrorReturn() - 错误时返回固定值
     *
     * @param request HTTP请求
     * @return 更新后的用户
     */
    public Mono<ServerResponse> updateUser(ServerRequest request) {
        String idStr = request.pathVariable("id");
        Long id;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return ServerResponse.badRequest()
                    .bodyValue(createError("无效的用户ID"));
        }

        Mono<User> userMono = request.bodyToMono(User.class);

        return userMono
                .flatMap(user -> userService.updateUser(id, user))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> ServerResponse.badRequest()
                        .bodyValue(createError(e.getMessage())))
                .log("Handler.updateUser");
    }

    /**
     * 删除用户
     *
     * 【DELETE请求的响应】
     *
     * 删除成功后：
     * - 返回204 No Content（推荐）
     * - 没有响应体
     *
     * 【then()操作符】
     *
     * then()用于在一个Mono完成后执行另一个操作：
     * - 先执行删除操作
     * - 删除完成后，返回204响应
     * - 不关心删除操作的结果（成功或失败）
     *
     * @param request HTTP请求
     * @return 204 No Content
     */
    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        String idStr = request.pathVariable("id");
        Long id;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return ServerResponse.badRequest()
                    .bodyValue(createError("无效的用户ID"));
        }

        return userService.deleteUser(id)
                .then(ServerResponse.noContent().build())
                .log("Handler.deleteUser");
    }

    /**
     * 搜索用户
     *
     * 【查询参数的处理】
     *
     * ServerRequest.queryParam()用于获取查询参数：
     * - queryParam("name") - 返回Optional<String>
     * - 如果参数不存在，返回Optional.empty()
     *
     * 【响应式分页】
     *
     * 响应式编程中实现分页的方式：
     * 1. skip() - 跳过前N个元素
     * 2. take() - 取前N个元素
     * 3. page() - Spring Data的响应式分页
     *
     * @param request HTTP请求
     * @return 匹配的用户列表
     */
    public Mono<ServerResponse> searchUsers(ServerRequest request) {
        String name = request.queryParam("name").orElse("");
        String limitStr = request.queryParam("limit").orElse("10");
        int limit;

        try {
            limit = Integer.parseInt(limitStr);
        } catch (NumberFormatException e) {
            limit = 10;
        }

        return userService.searchUsers(name, limit)
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(users))
                .log("Handler.searchUsers");
    }

    /**
     * 批量处理用户
     *
     * 【POST请求处理集合】
     *
     * 当请求体是数组时，使用bodyToFlux()：
     * - bodyToFlux(Long.class) - 将JSON数组反序列化为Flux<Long>
     * - 然后可以使用响应式操作符处理
     *
     * @param request HTTP请求
     * @return 处理结果
     */
    public Mono<ServerResponse> processUsers(ServerRequest request) {
        Flux<Long> userIds = request.bodyToFlux(Long.class);

        return userIds
                .collectList()  // 收集所有ID到列表
                .flatMap(ids -> userService.processUsers(ids)
                        .collectList())
                .flatMap(results -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(results))
                .log("Handler.processUsers");
    }

    /**
     * 错误信息辅助方法
     *
     * 【统一的错误响应格式】
     *
     * 创建一个错误响应Map：
     * - error: 错误消息
     * - message: 详细错误信息
     *
     * @param message 错误消息
     * @return 错误响应Map
     */
    private Map<String, String> createError(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "true");
        error.put("message", message);
        return error;
    }
}
