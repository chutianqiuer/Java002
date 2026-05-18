package com.example.repository;

import com.example.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 响应式用户仓库
 *
 * 【什么是响应式仓库？】
 *
 * 响应式仓库是响应式编程的核心组件之一。它与普通仓库的区别：
 *
 * 普通仓库（同步阻塞）:
     User findById(Long id);              // 阻塞等待，直到数据库返回结果
     List<User> findAll();                 // 阻塞等待，直到所有数据加载到内存
     void save(User user);                // 阻塞等待，直到保存完成
 *
 * 响应式仓库（异步非阻塞）:
     Mono<User> findById(Long id);         // 立即返回Mono，立即释放线程
     Flux<User> findAll();                 // 立即返回Flux，数据分批发出
     Mono<User> save(User user);          // 立即返回Mono，保存完成时发出数据
 *
 * 【本类的模拟实现】
 *
 * 为了简化示例，我们使用内存中的ConcurrentHashMap来模拟数据库。
 * 在实际项目中，可以使用以下响应式数据库：
 * - R2DBC (响应式关系型数据库)
 * - MongoDB Reactive Streams
 * - Redis Reactive (RedisClient)
 *
 * 【数据流示意】
 *
 * findAll()返回的Flux数据流：
 *
 * 时间轴:  --[User1]--[User2]--[User3]--[User4]--[User5]--|完成|
 *         |-------  慢速发出，每100ms一个  --------------|
 *
 * 调用者（Controller/Handler）可以：
 * - 使用map()转换每个用户
 * - 使用filter()过滤用户
 * - 使用take()只取前N个
 * - 使用collectList()收集所有到列表
 */
public class UserRepository {

    /**
     * 使用ConcurrentHashMap模拟数据库表
     * ConcurrentHashMap是线程安全的，适合高并发场景
     */
    private final Map<Long, User> userDb = new ConcurrentHashMap<>();

    /**
     * 构造函数 - 初始化一些示例数据
     * 使用静态代码块初始化模拟数据
     */
    public UserRepository() {
        // 添加一些测试用户
        userDb.put(1L, User.builder().id(1L).name("张三").email("zhangsan@example.com").age(25).build());
        userDb.put(2L, User.builder().id(2L).name("李四").email("lisi@example.com").age(30).build());
        userDb.put(3L, User.builder().id(3L).name("王五").email("wangwu@example.com").age(35).build());
        userDb.put(4L, User.builder().id(4L).name("赵六").email("zhaoliu@example.com").age(28).build());
        userDb.put(5L, User.builder().id(5L).name("孙七").email("sunqi@example.com").age(40).build());
    }

    /**
     * 根据ID查找用户
     *
     * 【返回类型Mono<User>】
     *
     * Mono<User>代表"0或1个User的异步序列"：
     * - 找到用户：发出1个User，然后完成
     * - 没找到用户：发出0个元素，然后完成（视为成功，只是没有数据）
     * - 发生错误：发出错误信号
     *
     * 【使用场景】
     * 适用于：
     * - GET /users/{id} 根据ID查询单个资源
     * - POST /users 创建一个资源后返回
     *
     * 【模拟延迟】
     * 使用delayElement()模拟数据库查询的延迟（100ms）
     * 这展示了响应式的"异步"特性
     */
    public Mono<User> findById(Long id) {
        return Mono.justOrEmpty(userDb.get(id))
                // delayElement模拟数据库查询延迟
                // 在实际应用中，这里会是真正的数据库查询
                .delayElement(Duration.ofMillis(100))
                // 给日志留出时间输出
                .log("Repository.findById");
    }

    /**
     * 查找所有用户
     *
     * 【返回类型Flux<User>】
     *
     * Flux<User>代表"0到N个User的异步序列"：
     * - 有数据：依次发出User1, User2, User3...，然后完成
     * - 无数据：发出0个元素，然后完成
     * - 发生错误：在任意点发出错误信号，终止流
     *
     * 【数据流特性】
     * Flux是一个"拉取式"的数据流：
     * - 下游（订阅者）向发布者"请求"数据
     * - 发布者按照下游的请求数量发送数据
     * - 这就是"背压（Backpressure）"机制
     *
     * 【使用场景】
     * 适用于：
     * - GET /users 返回用户列表
     * - GET /users/search 搜索结果列表
     */
    public Flux<User> findAll() {
        return Flux.fromIterable(userDb.values())
                // 模拟数据库查询延迟，每个用户之间延迟100ms
                // 这样可以更明显地观察数据流
                .delayElements(Duration.ofMillis(100))
                .log("Repository.findAll");
    }

    /**
     * 根据年龄范围查找用户
     *
     * 【filter操作符】
     *
     * filter()是响应式编程中最常用的操作符之一：
     * - 它接收一个Predicate（谓词）作为过滤条件
     * - 只有满足条件的元素才会被发出
     * - 不满足条件的元素被"丢弃"
     *
     * 【背压的意义】
     * 如果数据库一下子返回10000个用户，而客户端只能处理100个，
     * 没有背压的话会导致内存溢出。有了背压机制，
     * 客户端可以只请求它能处理的量（比如100个），避免问题。
     *
     * @param minAge 最小年龄（包含）
     * @param maxAge 最大年龄（包含）
     * @return 年龄在指定范围内的用户流
     */
    public Flux<User> findByAgeBetween(Integer minAge, Integer maxAge) {
        return Flux.fromIterable(userDb.values())
                .filter(user -> user.getAge() >= minAge && user.getAge() <= maxAge)
                .log("Repository.findByAgeBetween");
    }

    /**
     * 保存用户
     *
     * 【save操作】
     *
     * 响应式编程中，CRUD操作的返回类型：
     * - CREATE（创建）: Mono<User> - 返回创建后的用户
     * - READ（读取）: Mono<User> (单条) / Flux<User> (多条)
     * - UPDATE（更新）: Mono<User> - 返回更新后的用户
     * - DELETE（删除）: Mono<Void> - 不需要返回数据
     *
     * 【Mono<Void>的意义】
     * Mono<Void>表示"没有元素"的异步序列：
     * - 用于不需要返回数据的操作
     * - 仍然可以订阅，知道操作何时完成
     * - 可以用于确认删除成功
     *
     * @param user 要保存的用户
     * @return 保存后的用户（包含ID）
     */
    public Mono<User> save(User user) {
        // 如果没有ID，生成一个新的
        if (user.getId() == null) {
            long newId = userDb.keySet().stream()
                    .max(Long::compareTo)
                    .orElse(0L) + 1;
            user = User.builder()
                    .id(newId)
                    .name(user.getName())
                    .email(user.getEmail())
                    .age(user.getAge())
                    .build();
        }
        userDb.put(user.getId(), user);
        return Mono.just(user)
                .delayElement(Duration.ofMillis(50))
                .log("Repository.save");
    }

    /**
     * 根据ID删除用户
     *
     * 【删除操作的返回值】
     *
     * Mono<Void>表示操作完成但没有返回值：
     * - Mono<Void>不是"不执行"，而是"执行完了，没有数据要返回"
     * - 订阅 Mono<Void> 可以知道操作何时完成
     * - 即使是错误发生，也会通过onError信号通知订阅者
     *
     * @param id 要删除的用户ID
     * @return 完成信号
     */
    public Mono<Void> deleteById(Long id) {
        userDb.remove(id);
        return Mono.<Void>empty()
                .delayElement(Duration.ofMillis(50))
                .log("Repository.deleteById");
    }

    /**
     * 统计用户总数
     *
     * 【计数操作】
     *
     * count()是一个缩减操作（Terminal Operation）：
     * - 将Flux<User>缩减为Mono<Long>
     * - 数一下流中有多少个元素
     * - 类似的缩减操作还有：first(), last(), single(), reduce()
     *
     * 【重要：缩减操作会触发流的执行】
     * - map、filter等是惰性操作，不触发数据流
     * - count()是终端操作，会触发整个流的执行
     *
     * @return 用户总数
     */
    public Mono<Long> count() {
        return Mono.just((long) userDb.size())
                .log("Repository.count");
    }

    /**
     * 检查用户是否存在
     *
     * 【布尔操作】
     *
     * hasElements()检查流中是否有元素：
     * - 有元素：返回Mono.just(true)
     * - 无元素：返回Mono.just(false)
     *
     * 类似操作还有：
     * - exists() - 检查是否有满足条件的元素
     * - all() - 检查是否所有元素都满足条件
     *
     * @param id 用户ID
     * @return 是否存在
     */
    public Mono<Boolean> existsById(Long id) {
        return Mono.just(userDb.containsKey(id))
                .log("Repository.existsById");
    }
}
