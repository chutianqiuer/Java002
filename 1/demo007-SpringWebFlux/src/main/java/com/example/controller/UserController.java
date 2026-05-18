package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户控制器（注解式编程模型）
 *
 * 【什么是注解式编程模型？】
 *
 * 注解式编程模型是Spring WebFlux对传统Spring MVC的兼容。
 * 它使用熟悉的注解来定义端点：
 * - @RestController / @Controller
 * - @GetMapping / @PostMapping / @PutMapping / @DeleteMapping
 * - @PathVariable / @RequestBody / @RequestParam
 *
 * 【与Spring MVC的异同】
 *
 * | 特性         | Spring MVC                  | Spring WebFlux注解式           |
 * |-------------|-----------------------------|-------------------------------|
 * | 返回值处理    | 同步返回值                   | 可以返回Mono/Flux             |
 * | 线程模型     | 阻塞式                      | 非阻塞式                      |
 * | 异步支持     | 需要@Async等额外配置          | 原生支持响应式                |
 * | 数据库访问   | JdbcTemplate（阻塞）         | R2DBC（响应式）               |
 *
 * 【响应式返回值】
 *
 * 在Spring WebFlux的注解式控制器中，可以返回：
 * - 普通对象（如User） - Spring会自动包装为Mono
 * - Mono<User> - 单个用户
 * - Flux<User> - 用户列表
 * - ResponseEntity<Mono<User>> - 带状态码的响应
 *
 * 【为什么还要使用注解式？】
 *
 * 1. 渐进式迁移：更容易从Spring MVC迁移
 * 2. 团队熟悉度：团队成员更熟悉注解式开发
 * 3. 生态兼容：可以复用现有的Spring MVC知识和工具
 * 4. 某些场景更简单：简单的CRUD用注解更直接
 *
 * 【本控制器的路由】
 *
 * GET    /api/users          - 获取所有用户
 * GET    /api/users/{id}     - 获取单个用户
 * POST   /api/users          - 创建用户
 * PUT    /api/users/{id}     - 更新用户
 * DELETE /api/users/{id}     - 删除用户
 * GET    /api/users/search   - 搜索用户
 * GET    /api/users/count    - 获取用户数量
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 用户服务
     * 注入响应式服务
     */
    private final UserService userService;

    /**
     * 构造函数注入
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取所有用户
     *
     * 【Flux作为返回值】
     *
     * 返回Flux<User>表示：
     * - 这是一个0到N个用户的异步序列
     * - 数据会分批从HTTP响应中发出
     * - 客户端可以逐步接收数据，无需等待全部加载完成
     *
     * 【流式响应】
     *
     * Spring WebFlux会自动处理Flux的流式响应：
     * - 设置Transfer-Encoding: chunked
     * - 逐步发送数据，而不是一次性加载到内存
     * - 支持HTTP/2的服务器推送
     *
     * 【使用场景】
     * - GET /users - 获取用户列表
     * - GET /posts - 获取文章列表
     * - 任何需要返回集合的端点
     *
     * @return 所有用户的Flux流
     */
    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * 根据ID获取用户
     *
     * 【Mono作为返回值】
     *
     * 返回Mono<User>表示：
     * - 这是一个0或1个用户的异步序列
     * - 适合表示"可能存在也可能不存在"的单个资源
     *
     * 【@PathVariable的使用】
     *
     * @PathVariable用于从URL路径中提取变量：
     * - /users/{id}中的{id}会被提取
     * - 可以指定默认值：@PathVariable(required = false) Long id
     *
     * 【错误处理】
     *
     * 当用户不存在时：
     * - Spring会返回404 Not Found
     * - 或者使用ResponseEntity自定义状态码
     *
     * @param id 用户ID
     * @return 用户Mono
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                // map转换：将User转换为ResponseEntity
                .map(ResponseEntity::ok)
                // 没找到时返回404
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 创建用户
     *
     * 【@RequestBody的使用】
     *
     * @RequestBody用于接收请求体中的JSON数据：
     * - Spring会将JSON反序列化为User对象
     * - 使用Flux<User>或Mono<User>接收
     *
     * 【POST请求的响应】
     *
     * 创建成功后返回：
     * - 201 Created - HTTP标准状态码
     * - Location头 - 指向新创建的资源
     * - 响应体 - 创建的资源
     *
     * 【使用Mono获取请求体】
     *
     * @RequestBody Mono<User>表示：
     * - 请求体是异步发送的
     * - 需要等待整个请求体接收完毕后才能处理
     *
     * @param user 用户对象
     * @return 创建的用户
     */
    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody Mono<User> user) {
        return user
                .flatMap(userService::createUser)
                .map(createdUser -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(createdUser));
    }

    /**
     * 更新用户
     *
     * 【PUT请求的特点】
     *
     * PUT是幂等操作：
     * - 多次执行同样的更新，结果相同
     * - 可以安全地重试
     *
     * 【同时使用@PathVariable和@RequestBody】
     *
     * @PathVariable提取路径参数（用户ID）
     * @RequestBody接收更新数据（用户信息）
     *
     * @param id   用户ID
     * @param user 更新后的用户数据
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(
            @PathVariable Long id,
            @RequestBody Mono<User> user) {
        return user
                .flatMap(u -> userService.updateUser(id, u))
                .map(updatedUser -> ResponseEntity.ok(updatedUser))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 删除用户
     *
     * 【DELETE请求】
     *
     * 删除成功后：
     * - 返回204 No Content
     * - 没有响应体
     *
     * 【Mono<Void>的处理】
     *
     * 删除操作返回Mono<Void>：
     * - then()表示在操作完成后执行后续操作
     * - 这里用于返回204状态码
     *
     * @param id 用户ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }

    /**
     * 搜索用户
     *
     * 【@RequestParam的使用】
     *
     * @RequestParam用于获取查询参数：
     * - ?name=张三中的name参数
     * - 可以指定默认值：@RequestParam(defaultValue = "")
     * - 可以标记为可选：@RequestParam(required = false)
     *
     * 【GET请求的查询参数】
     *
     * GET /api/users/search?name=张&limit=10
     * - name=张 - 搜索名字中包含"张"的用户
     * - limit=10 - 最多返回10个结果
     *
     * @param name  搜索关键字（可选）
     * @param limit 最大返回数量（默认10）
     * @return 匹配的用户列表
     */
    @GetMapping("/search")
    public Mono<ResponseEntity<List<User>>> searchUsers(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(defaultValue = "10") int limit) {
        return userService.searchUsers(name, limit)
                .map(ResponseEntity::ok);
    }

    /**
     * 获取用户总数
     *
     * 【缩减操作】
     *
     * count()将Flux缩减为Mono：
     * - 计算流中元素的数量
     * - 返回Mono<Long>
     *
     * 【使用Mono.just()包装】
     *
     * ResponseEntity<Mono<Long>>表示：
     * - 响应体是一个Mono<Long>
     * - Spring会自动处理Mono，返回最终的值
     *
     * @return 用户总数
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> getUserCount() {
        return userService.getAllUsers()
                .count()
                .map(ResponseEntity::ok);
    }

    /**
     * 批量创建用户
     *
     * 【@RequestBody Flux<User>】
     *
     * 接收JSON数组：
     * [
     *   {"name": "张三", "email": "zhangsan@example.com", "age": 25},
     *   {"name": "李四", "email": "lisi@example.com", "age": 30}
     * ]
     *
     * 【flatMapMany】
     *
     * flatMapMany将Mono<Collection>转换为Flux：
     * - 先获取完整的集合（Mono）
     * - 再将集合展开为流（Flux）
     *
     * @param users 用户流
     * @return 创建的用户列表
     */
    @PostMapping("/batch")
    public Flux<User> batchCreateUsers(@RequestBody Flux<User> users) {
        return users
                .flatMap(userService::createUser);
    }

    /**
     * 测试响应式操作符
     *
     * 【在控制器层组合响应式操作】
     *
     * 这个端点演示了如何组合多个响应式操作：
     * - 先获取所有用户
     * - 过滤年龄大于25的
     * - 取前3个
     * - 转换为我们需要的格式
     *
     * 【注意：实际生产中应该在Service层做这些】
     *
     * @return 处理后的用户描述
     */
    @GetMapping("/demo/operators")
    public Flux<String> demoOperators() {
        return userService.getAllUsers()
                .filter(user -> user.getAge() > 25)  // 过滤年龄大于25
                .take(3)                              // 只取前3个
                .map(user -> String.format("%s (年龄:%d)", user.getName(), user.getAge())); // 转换
    }

    /**
     * 演示错误处理
     *
     * 【响应式错误处理】
     *
     * onErrorReturn()：发生错误时返回默认值
     * onErrorResume()：发生错误时切换到另一个流
     *
     * @param id 用户ID
     * @return 用户信息或错误消息
     */
    @GetMapping("/safe/{id}")
    public Mono<String> getUserSafe(@PathVariable Long id) {
        return userService.getUserSafe(id);
    }
}
