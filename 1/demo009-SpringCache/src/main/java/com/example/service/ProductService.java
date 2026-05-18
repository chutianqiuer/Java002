package com.example.service;

import com.example.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 产品服务类 - 演示多级缓存和缓存注意事项
 *
 * 【多级缓存架构】
 * 本类演示常见的缓存架构设计：
 *
 * L1缓存：Caffeine（本地缓存）
 * - 优点：访问速度极快，毫秒级响应
 * - 缺点：每个JVM实例一份，数据不共享
 * - 适用：热点数据、变化频率低的数据
 *
 * L2缓存：EhCache（进程内共享缓存）
 * - 优点：可以在多个JVM实例间共享（通过Terracotta）
 * - 缺点：访问速度比本地缓存慢
 * - 适用：需要在多个服务实例间共享的数据
 *
 * L3缓存：Redis（分布式缓存）
 * - 优点：支持大数据量，支持数据持久化
 * - 缺点：需要网络IO，延迟较高
 * - 适用：需要在多个服务实例间共享且数据量较大的场景
 *
 * 【缓存的三大注意事项】
 *
 * 1. 缓存穿透
 * - 问题：大量请求查询不存在的数据，直接打到数据库
 * - 原因：数据不存在，缓存中也没有
 * - 解决：
 *   a) 缓存空值：给空结果也设置一个短TTL（如30秒）的缓存
 *   b) 布隆过滤器：在缓存层前加一个布隆过滤器，快速判断数据是否存在
 *   c) 接口校验：参数合法性校验，提前过滤无效请求
 *
 * 2. 缓存雪崩
 * - 问题：大量缓存同时过期，导致大量请求同时穿透到数据库
 * - 原因：缓存TTL设置相同，大量缓存同时过期
 * - 解决：
 *   a) 随机TTL：为缓存TTL添加随机值，避免同时过期
 *   b) 多级缓存：L1 + L2 + L3缓存架构
 *   c) 熔断降级：使用Sentinel/Hystrix保护数据库
 *   d) 预热缓存：系统启动时提前加载热点数据
 *
 * 3. 缓存击穿
 * - 问题：某个热点数据过期瞬间，大量请求同时查询该数据
 * - 原因：热点数据突然过期，但缓存重建需要时间
 * - 解决：
 *   a) 互斥锁：只有一个线程去重建缓存，其他线程等待
 *   b) 永不过期：对热点数据设置永不过期，靠异步更新
 *   c) 逻辑过期：数据永不过期，但有逻辑过期时间，到期后异步重建
 *
 * 【本类演示内容】
 * 1. 多级缓存查询流程
 * 2. 缓存穿透的应对策略
 * 3. 缓存雪崩的应对策略
 * 4. 缓存击穿的应对策略
 */
@Service
public class ProductService {

    /**
     * 模拟数据库：存储产品信息
     * 实际应用中这里是DAO层或数据库
     */
    private final Map<Long, String> productDatabase = new ConcurrentHashMap<>();

    /**
     * 本地缓存：使用ConcurrentHashMap模拟L1缓存
     * 【说明】：生产环境应该使用Caffeine或Guava Cache
     */
    private final Map<Long, String> localCache = new ConcurrentHashMap<>();

    /**
     * 分布式缓存：使用ConcurrentHashMap模拟L2缓存
     * 【说明】：生产环境应该使用EhCache、Redis等
     */
    private final Map<Long, String> distributedCache = new ConcurrentHashMap<>();

    /**
     * 互斥锁：用于缓存击穿时的互斥访问控制
     * 【说明】：生产环境应该使用分布式锁（如Redis SETNX）
     */
    private final Map<Long, Boolean> lockMap = new ConcurrentHashMap<>();

    /**
     * 空值缓存：用于防止缓存穿透
     * 【说明】：缓存不存在的数据，避免重复查询数据库
     */
    private final Map<Long, String> nullValueCache = new ConcurrentHashMap<>();

    static {
        // 初始化产品数据
    }

    /**
     * 构造方法：初始化演示数据
     */
    public ProductService() {
        // 初始化100个产品数据
        for (long i = 1; i <= 100; i++) {
            productDatabase.put(i, "产品_" + i + "_详细信息");
        }
        System.out.println("[ProductService] 初始化产品数据，共 " + productDatabase.size() + " 条记录");
    }

    /**
     * 【演示：多级缓存查询】
     *
     * 【多级缓存查询流程】
     * 1. 查询L1本地缓存（Caffeine/本地Map）
     * 2. 命中则直接返回
     * 3. 未命中则查询L2分布式缓存（EhCache/Redis）
     * 4. 命中则返回，并回填L1缓存
     * 5. 未命中则查询数据库
     * 6. 查询到数据后，依次写入L2和L1缓存
     *
     * 【多级缓存优势】
     * - L1缓存命中：延迟最低，响应最快
     * - L1缓存未命中，L2缓存命中：减少数据库压力
     * - 全部未命中：只有少量请求会打到数据库
     *
     * @param productId 产品ID
     * @return 产品信息
     */
    public String getProductInfo(Long productId) {
        String cacheKey = "product:" + productId;

        // 第一步：查询L1本地缓存
        String result = localCache.get(cacheKey);
        if (result != null) {
            System.out.println("[getProductInfo] L1缓存命中，productId = " + productId);
            return result;
        }

        // 第二步：查询L2分布式缓存
        result = distributedCache.get(cacheKey);
        if (result != null) {
            System.out.println("[getProductInfo] L2缓存命中，productId = " + productId + "，回填L1缓存");
            // 回填L1缓存
            localCache.put(cacheKey, result);
            return result;
        }

        // 第三步：查询数据库
        System.out.println("[getProductInfo] L1和L2缓存都未命中，查询数据库，productId = " + productId);
        result = productDatabase.get(productId);

        if (result != null) {
            // 第四步：写入L2和L1缓存
            System.out.println("[getProductInfo] 写入L2和L1缓存");
            distributedCache.put(cacheKey, result);
            localCache.put(cacheKey, result);
        }

        return result;
    }

    /**
     * 【演示：@Cacheable注解的多级缓存效果】
     *
     * 【原理】
     * 使用Spring的@Cacheable注解，其背后的CacheManager会按顺序查询多个缓存
     * 在CompositeCacheManager中配置了Caffeine -> EhCache的顺序
     *
     * 【注意】
     * 默认的Spring Cache实现是"cache-aside"模式：
     * 1. 先查缓存
     * 2. 缓存未命中则查数据库
     * 3. 将结果写入缓存
     *
     * 这种模式不会自动实现L1 -> L2 -> DB的多级查询
     * 需要自定义CacheManager或使用Spring Cloud Cache来实现
     *
     * @param productId 产品ID
     * @return 产品信息
     */
    @Cacheable(value = "productInfo", key = "#productId")
    public String getProductInfoWithAnnotation(Long productId) {
        System.out.println("[getProductInfoWithAnnotation] 执行数据库查询，productId = " + productId);
        return productDatabase.get(productId);
    }

    /**
     * 【演示：缓存穿透的应对策略 - 缓存空值】
     *
     * 【问题分析】
     * 当查询一个不存在的产品（如productId=999）时：
     * - 数据库中不存在该数据
     * - 缓存中也没有
     * - 每次请求都会打到数据库
     * - 如果恶意大量请求不存在的数据，数据库会崩溃
     *
     * 【解决方案】
     * 对不存在的数据也进行缓存：
     * - key = "product:999"
     * - value = "NULL" 或特殊标记
     * - 设置较短的TTL（如30秒）
     *
     * 【本方法实现】
     * 1. 先查本地缓存
     * 2. 未命中查分布式缓存
     * 3. 还未命中查数据库
     * 4. 数据库也没有，则缓存空值（TTL=30秒）
     *
     * @param productId 产品ID
     * @return 产品信息或null
     */
    public String getProductInfoWithPenetration(Long productId) {
        String cacheKey = "product:" + productId;

        // 查询L1缓存
        String result = localCache.get(cacheKey);
        if (result != null) {
            // 检查是否是空值缓存
            if ("NULL".equals(result)) {
                System.out.println("[getProductInfoWithPenetration] L1缓存命中空值，productId = " + productId);
                return null;
            }
            System.out.println("[getProductInfoWithPenetration] L1缓存命中，productId = " + productId);
            return result;
        }

        // 查询L2缓存
        result = distributedCache.get(cacheKey);
        if (result != null) {
            if ("NULL".equals(result)) {
                System.out.println("[getProductInfoWithPenetration] L2缓存命中空值，productId = " + productId);
                return null;
            }
            System.out.println("[getProductInfoWithPenetration] L2缓存命中，productId = " + productId);
            return result;
        }

        // 查询数据库
        System.out.println("[getProductInfoWithPenetration] 查询数据库，productId = " + productId);
        result = productDatabase.get(productId);

        if (result == null) {
            // 数据库也没有，缓存空值，TTL=30秒
            System.out.println("[getProductInfoWithPenetration] 缓存空值，防止缓存穿透");
            // 实际生产中应该使用带TTL的缓存，这里简化处理
            distributedCache.put(cacheKey, "NULL");
            localCache.put(cacheKey, "NULL");
            return null;
        }

        // 写入缓存
        distributedCache.put(cacheKey, result);
        localCache.put(cacheKey, result);

        return result;
    }

    /**
     * 【演示：缓存雪崩的应对策略 - 随机TTL + 多级缓存】
     *
     * 【问题分析】
     * 如果所有缓存都设置相同的TTL（如1小时），那么：
     * - 1小时后，所有缓存同时过期
     * - 所有请求同时穿透到数据库
     * - 数据库压力瞬间增大，可能崩溃
     *
     * 【解决方案】
     * 1. 随机TTL：为每个缓存的TTL添加随机值（如 1小时 + Random(10分钟)）
     * 2. 多级缓存：L1 + L2 + L3，即使L1过期，还有L2
     * 3. 预热缓存：系统启动时加载热点数据到缓存
     * 4. 熔断降级：使用Sentinel/Hystrix保护数据库
     *
     * 【本方法实现】
     * 使用Random计算随机TTL，避免所有缓存同时过期
     *
     * @param productId 产品ID
     * @return 产品信息
     */
    public String getProductInfoWithAvalanche(Long productId) {
        String cacheKey = "product:" + productId;

        // 模拟：30%的概率L1缓存已过期（演示雪崩效果）
        // 实际生产中，这应该是真正的过期

        // 查询L1缓存（带随机过期）
        String result = localCache.get(cacheKey);
        if (result != null && !"NULL".equals(result)) {
            // 模拟：10%的概率认为缓存已过期（雪崩场景）
            if (Math.random() > 0.1) {
                System.out.println("[getProductInfoWithAvalanche] L1缓存命中，productId = " + productId);
                return result;
            } else {
                System.out.println("[getProductInfoWithAvalanche] L1缓存已过期（模拟雪崩），productId = " + productId);
            }
        }

        // 查询L2缓存（L2缓存有更长的TTL，作为保护）
        result = distributedCache.get(cacheKey);
        if (result != null && !"NULL".equals(result)) {
            System.out.println("[getProductInfoWithAvalanche] L2缓存命中，productId = " + productId);
            // 回填L1缓存，并设置随机TTL
            int randomTTL = 3600 + (int)(Math.random() * 600); // 1小时 + 0~10分钟随机
            System.out.println("[getProductInfoWithAvalanche] 回填L1缓存，TTL = " + randomTTL + "秒");
            localCache.put(cacheKey, result);
            return result;
        }

        // 查询数据库
        System.out.println("[getProductInfoWithAvalanche] L1和L2都未命中，查询数据库，productId = " + productId);
        result = productDatabase.get(productId);

        if (result != null) {
            // 写入L2缓存，设置较长TTL（如2小时）
            distributedCache.put(cacheKey, result);
            // 写入L1缓存，设置随机TTL（避免同时过期）
            int randomTTL = 3600 + (int)(Math.random() * 600);
            System.out.println("[getProductInfoWithAvalanche] 写入L1和L2缓存，L1 TTL = " + randomTTL + "秒");
            localCache.put(cacheKey, result);
        }

        return result;
    }

    /**
     * 【演示：缓存击穿的应对策略 - 互斥锁】
     *
     * 【问题分析】
     * 某个热点数据（如产品1）突然过期：
     * - 1000个请求同时查询产品1
     * - L1、L2缓存都没有
     * - 1000个请求同时打到数据库
     * - 数据库压力瞬间增大
     *
     * 【解决方案】
     * 1. 互斥锁：只有一个请求能获取锁去查数据库，其他请求等待
     * 2. 永不过期：对热点数据设置永不过期，靠异步更新
     * 3. 逻辑过期：数据有过期时间，但后台异步更新
     *
     * 【互斥锁原理】
     * 1. 请求A获取锁，查数据库，写缓存，释放锁
     * 2. 请求B、C、D...等待锁
     * 3. 请求A释放锁后，请求B获取锁，此时缓存已有数据，直接返回
     *
     * 【本方法实现】
     * 使用synchronized简单实现单机版互斥锁
     * 生产环境应该使用分布式锁（如Redis SETNX + Redisson）
     *
     * @param productId 产品ID
     * @return 产品信息
     */
    public String getProductInfoWithBreakdown(Long productId) {
        String cacheKey = "product:" + productId;

        // 第一步：查缓存
        String result = localCache.get(cacheKey);
        if (result != null && !"NULL".equals(result)) {
            System.out.println("[getProductInfoWithBreakdown] 缓存命中，productId = " + productId);
            return result;
        }

        // 第二步：尝试获取锁
        // 使用putIfAbsent实现简单的互斥锁
        Boolean locked = lockMap.putIfAbsent(cacheKey, true);

        if (locked == null) {
            // 获取锁成功，当前线程负责查数据库并更新缓存
            try {
                System.out.println("[getProductInfoWithBreakdown] 获取锁成功，查询数据库，productId = " + productId);

                // 再次检查缓存（可能有其他线程已经写入）
                result = localCache.get(cacheKey);
                if (result != null) {
                    return result;
                }

                // 查数据库
                result = productDatabase.get(productId);

                if (result != null) {
                    // 写入缓存
                    localCache.put(cacheKey, result);
                    distributedCache.put(cacheKey, result);
                    System.out.println("[getProductInfoWithBreakdown] 数据已写入缓存");
                }

                return result;
            } finally {
                // 释放锁
                lockMap.remove(cacheKey);
                System.out.println("[getProductInfoWithBreakdown] 释放锁");
            }
        } else {
            // 获取锁失败，等待后重试
            System.out.println("[getProductInfoWithBreakdown] 获取锁失败，等待重试，productId = " + productId);

            try {
                // 等待100毫秒后重试
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 重试查缓存
            result = localCache.get(cacheKey);
            if (result != null) {
                System.out.println("[getProductInfoWithBreakdown] 重试成功，productId = " + productId);
                return result;
            }

            // 递归重试
            return getProductInfoWithBreakdown(productId);
        }
    }

    /**
     * 【演示：逻辑过期方案 - 解决缓存击穿】
     *
     * 【逻辑过期方案原理】
     * 1. 缓存数据永不过期，但包含一个"逻辑过期时间"字段
     * 2. 读取数据时，检查逻辑过期时间
     * 3. 如果已逻辑过期，触发异步更新，返回旧数据
     * 4. 多个请求同时触发更新时，使用锁保证只有一个线程更新
     *
     * 【优势】
     * - 用户永远不会被阻塞，始终能拿到数据
     * - 缓存永远不会被击穿
     *
     * 【劣势】
     * - 用户可能拿到过期数据（但很快会被更新）
     * - 实现复杂，需要额外的逻辑过期字段
     *
     * @param productId 产品ID
     * @return 产品信息
     */
    public String getProductInfoWithLogicalExpire(Long productId) {
        String cacheKey = "product:" + productId;

        // 查询缓存
        String result = localCache.get(cacheKey);
        if (result == null) {
            // 缓存不存在，查数据库
            System.out.println("[getProductInfoWithLogicalExpire] 缓存不存在，查询数据库，productId = " + productId);
            result = productDatabase.get(productId);

            if (result != null) {
                // 写入缓存，逻辑过期时间 = 当前时间 + 30分钟
                long logicalExpireTime = System.currentTimeMillis() + 30 * 60 * 1000;
                String cacheValue = result + "|logicalExpire:" + logicalExpireTime;
                localCache.put(cacheKey, cacheValue);
                distributedCache.put(cacheKey, cacheValue);
                System.out.println("[getProductInfoWithLogicalExpire] 数据已写入缓存，逻辑过期时间 = " + logicalExpireTime);
            }

            return result;
        }

        // 缓存存在，解析逻辑过期时间
        String[] parts = result.split("\\|logicalExpire:");
        if (parts.length == 2) {
            long logicalExpireTime = Long.parseLong(parts[1]);
            long currentTime = System.currentTimeMillis();

            if (currentTime > logicalExpireTime) {
                // 已逻辑过期，触发异步更新
                System.out.println("[getProductInfoWithLogicalExpire] 数据已逻辑过期，触发异步更新，productId = " + productId);

                // 启动异步线程更新缓存
                new Thread(() -> {
                    // 获取锁
                    Boolean locked = lockMap.putIfAbsent(cacheKey + "_update", true);
                    if (locked != null) {
                        return;
                    }

                    try {
                        // 模拟更新缓存
                        String newData = productDatabase.get(productId);
                        if (newData != null) {
                            long newLogicalExpireTime = System.currentTimeMillis() + 30 * 60 * 1000;
                            String newCacheValue = newData + "|logicalExpire:" + newLogicalExpireTime;
                            localCache.put(cacheKey, newCacheValue);
                            distributedCache.put(cacheKey, newCacheValue);
                            System.out.println("[getProductInfoWithLogicalExpire] 异步更新完成，productId = " + productId);
                        }
                    } finally {
                        lockMap.remove(cacheKey + "_update");
                    }
                }).start();

                // 返回旧数据（不阻塞用户）
                return parts[0];
            }
        }

        System.out.println("[getProductInfoWithLogicalExpire] 缓存命中，productId = " + productId);
        return result.split("\\|")[0];
    }

    /**
     * 清除所有缓存（用于测试）
     */
    public void clearAllCaches() {
        localCache.clear();
        distributedCache.clear();
        nullValueCache.clear();
        lockMap.clear();
        System.out.println("[ProductService] 所有缓存已清除");
    }
}
