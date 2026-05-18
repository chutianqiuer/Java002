package com.example.service;

import com.example.model.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务类 - 演示Spring Cache核心注解
 *
 * 【Spring Cache核心注解详解】
 *
 * 1. @Cacheable：【缓存查询】
 *    - 执行方法前先检查缓存，缓存命中则直接返回，不执行方法
 *    - 缓存未命中则执行方法，并将结果放入缓存
 *    - 适用场景：读取频繁、数据变化少的数据（如配置信息、字典数据）
 *
 * 2. @CacheEvict：【缓存清除】
 *    - 执行方法后清除缓存
 *    - 可以清除一条缓存（指定key）或清除所有缓存（allEntries=true）
 *    - 适用场景：数据更新时删除旧缓存，保证数据一致性
 *
 * 3. @CachePut：【缓存更新】
 *    - 每次都执行方法，并更新缓存
 *    - 与@Cacheable区别：无论缓存是否存在，都执行方法
 *    - 适用场景：需要每次都获取最新数据的场景
 *
 * 4. @Caching：【组合注解】
 *    - 可以在一个方法上使用多个缓存操作
 *    - 例如：同时执行@CacheEvict和@CachePut
 *
 * 5. @CacheConfig：【类级别公共配置】
 *    - 统一指定cacheNames、keyGenerator等公共属性
 *    - 方法级别的注解会继承或覆盖类级别配置
 *
 * 【SpEL表达式在缓存注解中的应用】
 * - #id：直接引用方法参数
 * - #user.id：引用方法参数的属性
 * - #result：引用方法返回值
 * - #root.methodName：获取方法名
 * - #root.targetClass：获取目标类
 *
 * 【缓存条件控制】
 * - condition：方法执行前判断，决定是否缓存
 * - unless：方法执行后判断，决定是否不缓存
 *
 * 【本类演示内容】
 * 1. 基本的@Cacheable使用
 * 2. 带有SpEL表达式的@Cacheable
 * 3. @CacheEvict清除缓存
 * 4. @CachePut更新缓存
 * 5. @Caching组合多个缓存操作
 * 6. condition和unless条件控制
 * 7. @CacheConfig类级别配置
 */
@Service
// @CacheConfig：类级别公共缓存配置
// 所有方法如果没有特殊指定，都使用这个cacheNames
@CacheConfig(cacheNames = "users")
public class UserService {

    /**
     * 模拟数据库：使用ConcurrentHashMap存储用户数据
     * 【说明】：实际应用中这里是DAO层或数据库
     */
    private final Map<Long, User> userDatabase = new ConcurrentHashMap<>();

    /**
     * 静态初始化一些用户数据
     * 【演示数据准备】
     */
    public UserService() {
        userDatabase.put(1L, new User(1L, "张三", "zhangsan@example.com", 25));
        userDatabase.put(2L, new User(2L, "李四", "lisi@example.com", 30));
        userDatabase.put(3L, new User(3L, "王五", "wangwu@example.com", 28));
        System.out.println("[UserService] 初始化用户数据，共 " + userDatabase.size() + " 条记录");
    }

    /**
     * 【演示@Cacheable基本用法】
     *
     * 【注解参数说明】
     * - value/cacheNames：缓存名称，必填
     * - key：缓存key，支持SpEL表达式，默认为方法参数
     * - unless：条件表达式，为true时不缓存
     * - condition：条件表达式，为false时不缓存
     *
     * 【执行流程】
     * 1. 检查缓存中是否存在key为"1"的缓存
     * 2. 存在：直接返回缓存值，不执行方法
     * 3. 不存在：执行方法，将返回值存入缓存，返回结果
     *
     * 【SpEL表达式详解】
     * - "#id"：直接引用方法参数id
     * - "#p0"：引用第一个参数（p0表示parameter 0）
     * - "#result"：引用返回值（只能在unless中使用）
     * - "#root.methodName"：获取当前方法名
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @Cacheable(value = "user", key = "#id")
    public User getUserById(Long id) {
        System.out.println("[getUserById] 执行数据库查询，id = " + id);
        // 模拟数据库查询延迟
        simulateDbDelay();
        return userDatabase.get(id);
    }

    /**
     * 【演示@Cacheable + condition条件控制】
     *
     * 【condition参数】
     * - 在方法执行前判断
     * - condition="#id > 0"表示只有id > 0时才缓存
     * - 用于过滤不适合缓存的请求
     *
     * 【使用场景】
     * - 过滤非法参数（如id <= 0）
     * - 根据参数值决定是否缓存（如管理员请求不缓存）
     * - 根据业务状态决定是否缓存（如系统维护模式不缓存）
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @Cacheable(value = "userCondition", key = "#id", condition = "#id > 0")
    public User getUserByIdWithCondition(Long id) {
        System.out.println("[getUserByIdWithCondition] 执行数据库查询，id = " + id);
        simulateDbDelay();
        return userDatabase.get(id);
    }

    /**
     * 【演示@Cacheable + unless条件控制】
     *
     * 【unless参数】
     * - 在方法执行后判断
     * - unless="#result == null"表示返回值为null时不缓存
     * - 用于过滤空结果，避免缓存空值（缓存穿透问题）
     *
     * 【缓存穿透问题】
     * - 查询不存在的数据时，每次都查询数据库
     * - 恶意攻击：大量请求不存在的数据，可能导致数据库崩溃
     * - 解决方案：unless="#result == null"不缓存空结果
     * - 但更好的方案是使用布隆过滤器或缓存空值（空值也要缓存，设置短过期时间）
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @Cacheable(value = "userUnless", key = "#id", unless = "#result == null")
    public User getUserByIdWithUnless(Long id) {
        System.out.println("[getUserByIdWithUnless] 执行数据库查询，id = " + id);
        simulateDbDelay();
        return userDatabase.get(id);
    }

    /**
     * 【演示@Cacheable使用自定义keyGenerator生成的key】
     *
     * 【Key生成规则】
     * 在CacheConfig中配置了自定义KeyGenerator
     * 生成的key格式：UserService.getUserByIdWithGenerator:1
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @Cacheable(value = "userGenerator", keyGenerator = "keyGenerator")
    public User getUserByIdWithGenerator(Long id) {
        System.out.println("[getUserByIdWithGenerator] 执行数据库查询，id = " + id);
        simulateDbDelay();
        return userDatabase.get(id);
    }

    /**
     * 【演示@CachePut更新缓存】
     *
     * 【@CachePut vs @Cacheable区别】
     * - @Cacheable：缓存不存在才执行方法，存在则直接返回缓存
     * - @CachePut：每次都执行方法，并更新缓存
     *
     * 【使用场景】
     * - 数据更新方法：更新后需要返回最新数据
     * - 适合：写入后立即读取的场景
     * - 注意：@CachePut不会检查缓存，纯粹用于更新
     *
     * 【key表达式】
     * - "#user.id"：从user参数中获取id属性作为key
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    @CachePut(value = "user", key = "#user.id")
    public User updateUser(User user) {
        System.out.println("[updateUser] 执行数据库更新，user = " + user);
        simulateDbDelay();

        if (userDatabase.containsKey(user.getId())) {
            userDatabase.put(user.getId(), user);
            System.out.println("[updateUser] 更新成功");
            return user;
        }

        System.out.println("[updateUser] 用户不存在，id = " + user.getId());
        return null;
    }

    /**
     * 【演示@CacheEvict清除缓存】
     *
     * 【@CacheEvict参数】
     * - allEntries：是否清除所有缓存，默认为false
     * - beforeInvocation：是否在方法执行前清除，默认为false
     *
     * 【allEntries=true场景】
     * - 批量删除操作后，清空整表缓存
     * - 缓存数据量较大，逐个删除效率低
     * - 数据一致性要求高，不允许旧数据存在
     *
     * 【beforeInvocation=true场景】
     * - 方法执行可能抛出异常，但仍然需要清除缓存
     * - 保证缓存一致性：无论方法成功与否，都清除缓存
     *
     * @param id 用户ID
     */
    @CacheEvict(value = "user", key = "#id")
    public void deleteUser(Long id) {
        System.out.println("[deleteUser] 执行数据库删除，id = " + id);
        simulateDbDelay();
        userDatabase.remove(id);
        System.out.println("[deleteUser] 删除成功");
    }

    /**
     * 【演示@CacheEvict清除所有缓存】
     *
     * 【场景说明】
     * - 当需要清空某个缓存区的所有数据时使用
     * - 例如：系统重置、角色权限变更、全量数据导入
     *
     * 【慎用allEntries=true】
     * - 会清除指定cacheNames下的所有缓存
     * - 可能造成缓存雪崩：大量请求同时穿透到数据库
     * - 建议：结合消息队列，异步清除缓存
     *
     * @param userId 用户ID（此处参数用于日志记录）
     */
    @CacheEvict(value = "user", allEntries = true)
    public void clearAllUsers(Long userId) {
        System.out.println("[clearAllUsers] 清除users缓存区所有缓存，触发操作的用户id = " + userId);
        // 实际业务中可能是：清除所有用户缓存，强制重新加载
        System.out.println("[clearAllUsers] 缓存清除完成");
    }

    /**
     * 【演示@Caching组合多个缓存操作】
     *
     * 【@Caching使用场景】
     * - 需要同时执行多个缓存操作时
     * - 例如：更新用户信息后，需要：
     *   1. 更新用户详情缓存（@CachePut）
     *   2. 清除用户列表缓存（@CacheEvict）
     *   3. 更新用户统计缓存（@CachePut）
     *
     * 【@CachePut和@CacheEvict组合】
     * - @CachePut：更新用户的详细缓存
     * - @CacheEvict：清除用户的列表缓存（可能包含该用户）
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    @Caching(
        put = {
            // 更新用户详情缓存
            @CachePut(value = "userDetail", key = "#user.id"),
            // 更新用户索引缓存（用name作为key）
            @CachePut(value = "userIndex", key = "#user.name")
        },
        evict = {
            // 清除用户列表缓存
            @CacheEvict(value = "userList", allEntries = true)
        }
    )
    public User updateUserAndEvictList(User user) {
        System.out.println("[updateUserAndEvictList] 执行更新并清除列表缓存，user = " + user);
        simulateDbDelay();

        if (userDatabase.containsKey(user.getId())) {
            userDatabase.put(user.getId(), user);
            System.out.println("[updateUserAndEvictList] 更新成功");
            return user;
        }

        return null;
    }

    /**
     * 【演示获取缓存统计信息】
     *
     * 【缓存命中率】
     * - hit = 缓存命中 = 从缓存获取数据
     * - miss = 缓存未命中 = 需要执行方法获取数据
     * - hitRate = hit / (hit + miss) = 命中率
     *
     * 【性能监控指标】
     * - 命中率：越高越好，说明缓存效率高
     * - 加载时间：缓存未命中时，加载数据的时间
     * - 淘汰次数：缓存满时淘汰旧数据的次数
     *
     * 【Caffeine统计信息获取】
     * CaffeineCacheManager.getCache("users").getNativeCache().stats()
     */
    public void printCacheStats() {
        System.out.println("========== 缓存统计信息 ==========");
        // 获取Caffeine缓存统计
        var cacheManager = new org.springframework.cache.caffeine.CaffeineCacheManager("users");
        var nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>)
            cacheManager.getCache("users").getNativeCache();

        var stats = nativeCache.stats();
        System.out.println("命中率: " + stats.hitRate());
        System.out.println("命中次数: " + stats.hitCount());
        System.out.println("未命中次数: " + stats.missCount());
        System.out.println("加载时间（平均）: " + stats.averageLoadPenalty() + "ms");
        System.out.println("=================================");
    }

    /**
     * 【模拟数据库查询延迟】
     * 用于演示缓存效果：没有缓存时每次查询需要等待
     */
    private void simulateDbDelay() {
        try {
            // 模拟100毫秒的数据库查询延迟
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取所有用户（用于测试）
     */
    public Map<Long, User> getAllUsers() {
        return new HashMap<>(userDatabase);
    }
}
