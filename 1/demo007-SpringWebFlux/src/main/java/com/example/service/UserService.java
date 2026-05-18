package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 响应式用户服务层
 *
 * 【服务层在响应式架构中的位置】
 *
 * 传统三层架构：
 * Controller -> Service -> Repository -> Database
 *    |           |          |            |
 *   同步         同步        同步         同步
 *
 * 响应式三层架构：
 * Controller -> Service -> Repository -> Database
 *    |           |          |            |
 *   异步         异步        异步         异步/响应式
 *
 * 【关键点：整个调用链必须都是响应式的】
 *
 * 如果Service层用了响应式（返回Mono/Flux），
 * 但Repository层是同步阻塞的（返回User/List），
 * 那么Service层就必须使用block()来转换，
 * 这会破坏响应式链路，失去非阻塞的优势。
 *
 * 【本服务的职责】
 *
 * 1. 封装业务逻辑
 * 2. 组合多个响应式操作
 * 3. 转换数据格式
 * 4. 实现业务规则验证
 */
public class UserService {

    /**
     * 注入响应式仓库
     * 注意：这里注入的是UserRepository，而不是普通的UserDao
     */
    private final UserRepository userRepository;

    /**
     * 构造函数注入
     * Spring会自动注入UserRepository的实例
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取所有用户
     *
     * 【map操作符 - 数据转换】
     *
     * map()用于同步转换数据：
     * - 输入：User对象
     * - 输出：User对象的某种转换形式
     * - 特点：一对一转换，每个输入元素产生一个输出元素
     *
     * 【示例解释】
     * Flux.fromIterable(users)  // 假设有3个用户
     *     .map(User::getName)   // 转换为: Flux<String> = "张三", "李四", "王五"
     *
     * 【使用场景】
     * - 实体转DTO
     * - 格式转换（如日期格式）
     * - 字段提取
     *
     * @return 所有用户的Flux流
     */
    public Flux<User> getAllUsers() {
        return userRepository.findAll()
                .map(user -> {
                    // 这里可以对每个用户进行转换
                    // 例如：实体转DTO，去掉敏感字段等
                    return user;
                })
                .log("Service.getAllUsers");
    }

    /**
     * 根据ID获取用户
     *
     * 【Mono的使用场景】
     *
     * Mono适合以下情况：
     * 1. 已知结果是单个值或空（0或1）
     * 2. 需要明确区分"找到了"和"没找到"
     * 3. 异步操作返回单个结果
     *
     * 【flatMap操作符 - 异步转换】
     *
     * flatMap()用于异步转换：
     * - 输入：User
     * - 输出：Mono<Something>（异步操作的结果）
     * - 特点：可以处理嵌套的Mono/Flux
     *
     * 为什么用flatMap而不是map？
     * 因为某些业务逻辑可能需要异步执行，比如：
     * - 查询用户的订单（订单服务是异步的）
     * - 查询用户的权限（权限服务是异步的）
     * - 发送通知（消息队列是异步的）
     *
     * @param id 用户ID
     * @return 用户Mono
     */
    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    // 假设这里需要查询用户的额外信息（比如角色）
                    // 这些查询是异步的，所以用flatMap
                    // Mono.just()只是演示，返回原用户
                    return Mono.just(user);
                })
                .log("Service.getUserById");
    }

    /**
     * 创建用户
     *
     * 【响应式CRUD】
     *
     * CREATE操作流程：
     * 1. 验证输入数据
     * 2. 调用repository保存
     * 3. 返回保存后的数据
     *
     * 【数据验证】
     * 响应式编程中的验证也是响应式的：
     * - 使用Mono.justOrEmpty()处理可能的空值
     * - 使用filter()进行业务规则验证
     * - 使用flatMap()串联多个验证步骤
     *
     * @param user 要创建的用户
     * @return 创建后的用户Mono
     */
    public Mono<User> createUser(User user) {
        // 业务验证：检查必填字段
        if (user.getName() == null || user.getName().isBlank()) {
            return Mono.error(new IllegalArgumentException("用户名不能为空"));
        }

        // 检查邮箱格式（简单验证）
        if (user.getEmail() != null && !user.getEmail().contains("@")) {
            return Mono.error(new IllegalArgumentException("邮箱格式不正确"));
        }

        // 业务验证：检查年龄范围
        if (user.getAge() != null && (user.getAge() < 0 || user.getAge() > 150)) {
            return Mono.error(new IllegalArgumentException("年龄必须在0-150之间"));
        }

        // 调用仓库保存
        return userRepository.save(user)
                .log("Service.createUser");
    }

    /**
     * 更新用户
     *
     * 【更新操作的响应式模式】
     *
     * 响应式更新的一般模式：
     * 1. 先查询是否存在（findById）
     * 2. 如果不存在，返回错误
     * 3. 如果存在，执行更新
     *
     * 【switchIfEmpty操作符】
     *
     * switchIfEmpty()用于处理空序列：
     * - 如果原始序列为空，切换到备用序列
     * - 如果原始序列有数据，忽略备用序列
     *
     * 使用场景：
     * - "如果没找到用户，就返回错误"
     * - "如果缓存为空，就从数据库查询"
     *
     * @param id      用户ID
     * @param newUser 新的用户数据
     * @return 更新后的用户
     */
    public Mono<User> updateUser(Long id, User newUser) {
        // 先查询用户是否存在
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("用户不存在")))
                .flatMap(existingUser -> {
                    // 执行更新
                    User updatedUser = User.builder()
                            .id(id)
                            .name(newUser.getName() != null ? newUser.getName() : existingUser.getName())
                            .email(newUser.getEmail() != null ? newUser.getEmail() : existingUser.getEmail())
                            .age(newUser.getAge() != null ? newUser.getAge() : existingUser.getAge())
                            .build();
                    return userRepository.save(updatedUser);
                })
                .log("Service.updateUser");
    }

    /**
     * 删除用户
     *
     * 【Mono<Void>的处理】
     *
     * 删除操作通常返回Mono<Void>：
     * - 表示操作完成，没有返回值
     * - 订阅后可以知道操作何时完成
     *
     * 【then操作符】
     *
     * then()用于在一个操作完成后执行另一个操作：
     * - 删除成功后，发送一封通知邮件
     * - 清理完成后，记录日志
     *
     * @param id 用户ID
     * @return 完成信号
     */
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id)
                .doOnSuccess(ignored -> System.out.println("用户已删除: " + id))
                .log("Service.deleteUser");
    }

    /**
     * 复杂查询：获取用户列表并转换
     *
     * 【zip操作符 - 组合多个流】
     *
     * zip()用于组合多个数据流：
     * - 输入：多个Flux/Mono
     * - 输出：每个源发出一个元素后，组合成一个元组
     * - 特点：一对一配对，等待所有源都有数据才发出
     *
     * 【使用场景】
     * - 同时查询用户信息和订单信息，然后组合
     * - 同时查询多个数据源，然后合并结果
     * - 同时执行多个验证，汇总验证结果
     *
     * @param age 年龄过滤条件
     * @return 用户信息列表
     */
    public Flux<String> getUserDescriptions(Integer age) {
        // 获取所有用户
        Flux<User> allUsers = userRepository.findAll();

        // 获取用户总数
        Mono<Long> userCount = userRepository.count();

        // 如果指定了年龄，按年龄过滤
        Flux<User> filteredUsers = age != null
                ? userRepository.findByAgeBetween(age - 10, age + 10)
                : allUsers;

        // 使用zip组合：用户列表 + 总数
        // 将组合结果按行拆分成Flux
        return Flux.zip(
                filteredUsers.collectList(),
                userCount,
                (users, totalCount) -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("用户列表（共%d人，总计%d人）：\n",
                            users.size(), totalCount));
                    for (User user : users) {
                        sb.append(String.format("  - %s (ID:%d, 年龄:%d)\n",
                                user.getName(), user.getId(), user.getAge()));
                    }
                    return sb.toString();
                }
        ).flatMap(description -> Flux.fromArray(description.split("\n")));
    }

    /**
     * 批量处理用户
     *
     * 【flatMap vs concatMap】
     *
     * flatMap：并行处理，每个元素触发一个异步操作
     * concatMap：顺序处理，按顺序执行每个异步操作
     * switchMap：切换处理，只处理最新的异步操作
     *
     * 【本方法的处理逻辑】
     * - 对每个用户执行一个"处理"操作
     * - 使用flatMap实现并行处理
     * - 使用delayElements模拟处理延迟
     *
     * @param userIds 用户ID列表
     * @return 处理结果
     */
    public Flux<String> processUsers(List<Long> userIds) {
        return Flux.fromIterable(userIds)
                // 使用flatMap并行处理每个用户
                .flatMap(id -> userRepository.findById(id)
                        .map(user -> "处理用户: " + user.getName())
                        .defaultIfEmpty("用户不存在: " + id)
                        .flux()  // 转换为Flux以便使用delayElements
                        .delayElements(java.time.Duration.ofMillis(50))
                )
                .log("Service.processUsers");
    }

    /**
     * 条件查询：搜索用户
     *
     * 【filter + take + 缩减操作】
     *
     * filter()：过滤满足条件的元素
     * take()：只取前N个元素
     * collectList()：收集所有元素到列表
     *
     * 【搜索模式】
     * - 先过滤不符合条件的
     * - 限制返回数量（分页）
     * - 收集结果返回
     *
     * @param name 用户名（模糊匹配）
     * @param maxResults 最大返回数量
     * @return 匹配的用户列表
     */
    public Mono<List<User>> searchUsers(String name, int maxResults) {
        return userRepository.findAll()
                .filter(user -> user.getName().contains(name))
                .take(maxResults)  // 限制返回数量，实现分页
                .collectList()      // 收集到List
                .log("Service.searchUsers");
    }

    /**
     * 错误处理示例
     *
     * 【响应式错误处理】
     *
     * 响应式编程中的错误处理与命令式不同：
     * - 错误被视为"信号"，不是"异常"
     * - 错误沿流向上传播，直到被捕获处理
     * - 使用onErrorResume()、onErrorReturn()等处理错误
     *
     * @param id 用户ID
     * @return 用户或错误信息
     */
    public Mono<String> getUserSafe(Long id) {
        return userRepository.findById(id)
                .map(user -> "找到用户: " + user.getName())
                // 如果原始流为空，切换到备用流
                .switchIfEmpty(Mono.just("用户不存在"))
                // 如果发生错误，返回错误信息
                .onErrorResume(e -> Mono.just("发生错误: " + e.getMessage()))
                .log("Service.getUserSafe");
    }
}
