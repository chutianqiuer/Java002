package com.example.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.cache.support.CompositeCacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存配置类
 *
 * 【Spring Cache配置核心概念】
 *
 * 1. CacheManager（缓存管理器）：
 *    - Spring Cache的核心接口，负责管理缓存的存储、读取和删除
 *    - 不同的缓存实现（EhCache、Caffeine、Redis等）都有对应的CacheManager实现
 *    - 可以配置多个CacheManager，形成多级缓存
 *
 * 2. Cache（缓存）：
 *    - CacheManager创建的缓存实例
 *    - 类似于Map结构，存储key-value对
 *    - 每个Cache有独立的名称，可以配置不同的过期时间、容量等
 *
 * 3. KeyGenerator（key生成器）：
 *    - 用于生成缓存的key
 *    - 默认实现是SimpleKeyGenerator，规则如下：
 *      - 如果没有参数：返回SimpleKey.EMPTY
 *      - 如果只有一个参数：返回该参数本身
 *      - 如果有多个参数：返回包含所有参数的SimpleKey
 *    - 可以自定义实现，支持SpEL表达式
 *
 * 【本配置类演示内容】
 * 1. 配置EhCache 3.x缓存管理器
 * 2. 配置Caffeine缓存管理器
 * 3. 配置ConcurrentHashMap缓存管理器（简单实现）
 * 4. 配置CompositeCacheManager（多级缓存管理器）
 * 5. 自定义KeyGenerator
 *
 * 【多级缓存设计】
 * CompositeCacheManager允许配置多个缓存管理器，形成缓存链：
 * - 查询时按顺序查询每个缓存，找到即返回
 * - 写入时写入第一个缓存管理器
 * 这种设计可以实现"L1本地缓存 + L2分布式缓存"等多级缓存架构
 */
@Configuration
public class CacheConfig implements CachingConfigurer {

    /**
     * 定义缓存管理器列表，用于组成多级缓存
     * 【多级缓存策略】
     * 先从本地缓存（Caffeine）查询，未命中则查询分布式缓存（EhCache）
     * 这样可以减少对分布式缓存的访问压力，提高响应速度
     */
    private List<CacheManager> cacheManagers = new ArrayList<>();

    /**
     * 配置默认的缓存管理器
     *
     * 【@Primary注解作用】
     * 当有多个CacheManager bean时，@Primary标记的会被默认使用
     * 其他需要显式指定使用哪个CacheManager
     *
     * @return 组合缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        // 创建组合缓存管理器，支持多级缓存
        CompositeCacheManager cacheManager = new CompositeCacheManager();

        // 设置缓存管理器列表
        // 查询顺序：先查Caffeine本地缓存，再查EhCache分布式缓存
        List<CacheManager> managers = new ArrayList<>();

        // 第一级：Caffeine本地缓存（高性能，进程内）
        managers.add(caffeineCacheManager());
        System.out.println("[CacheConfig] 配置第一级缓存：Caffeine本地缓存");

        // 第二级：EhCache分布式缓存（可以跨JVM共享）
        managers.add(ehCacheCacheManager());
        System.out.println("[CacheConfig] 配置第二级缓存：EhCache分布式缓存");

        cacheManager.setCacheManagers(managers);

        return cacheManager;
    }

    /**
     * 配置Caffeine缓存管理器
     *
     * 【Caffeine缓存特点】
     * 1. 高性能：基于ConcurrentHashMap，线程安全
     * 2. 丰富的过期策略：访问时间、写入时间、依赖关系等
     * 3. 丰富的淘汰算法：LRU、LFU、FIFO等
     * 4. 统计信息：缓存命中率、加载时间等
     * 5. 异步加载：支持异步方式加载缓存
     *
     * 【Caffeine配置参数说明】
     * - initialCapacity：初始容量
     * - maximumSize：最大容量，超过后触发淘汰
     * - expireAfterWrite：写入后过期时间
     * - expireAfterAccess：访问后过期时间
     * - recordStats：记录统计信息，用于监控
     *
     * @return Caffeine缓存管理器
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        com.github.benmanes.caffeine.cache.Cache<Object, Object> cache =
            com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                // 初始容量：缓存容量的初始大小
                .initialCapacity(100)
                // 最大容量：当缓存超过这个数量时，会根据淘汰策略淘汰
                .maximumSize(1000)
                // 写入后过期时间：数据写入后30秒过期
                // 【适用场景】：数据变化频率较低，不需要实时更新
                .expireAfterWrite(30, java.util.concurrent.TimeUnit.SECONDS)
                // 访问后过期时间：最后一次访问后60秒过期
                // 【适用场景】：热点数据，需要根据访问频率动态管理
                .expireAfterAccess(60, java.util.concurrent.TimeUnit.SECONDS)
                // 记录统计信息：开启后可以通过cache.stats()获取命中率等
                .recordStats()
                // 构建缓存实例
                .build();

        // 使用Spring的CacheManager接口包装Caffeine
        org.springframework.cache.caffeine.CaffeineCacheManager caffeineCacheManager =
            new org.springframework.cache.caffeine.CaffeineCacheManager("users", "products");

        // 设置实际的缓存实现
        caffeineCacheManager.setCaffeine(cache);

        System.out.println("[CacheConfig] Caffeine缓存管理器创建完成");
        System.out.println("  - 初始容量: 100");
        System.out.println("  - 最大容量: 1000");
        System.out.println("  - 写入过期: 30秒");
        System.out.println("  - 访问过期: 60秒");

        return caffeineCacheManager;
    }

    /**
     * 配置EhCache缓存管理器
     *
     * 【EhCache缓存特点】
     * 1. 成熟的缓存框架：从Hibernate二级缓存发展而来
     * 2. 多种配置方式：XML配置、程序配置
     * 3. 支持持久化：可以将缓存数据持久化到磁盘
     * 4. 支持分布式：Ehcache 3.x支持Terracotta集群
     * 5. JCache标准实现：符合JSR-107规范
     *
     * 【EhCache vs Caffeine选择建议】
     * - 单应用、追求性能：选择Caffeine
     * - 分布式、需要持久化：选择EhCache
     * - 需要标准JCache API：选择EhCache（同时需要引入javax.cache:cache-api）
     *
     * @return EhCache缓存管理器
     */
    @Bean
    public CacheManager ehCacheCacheManager() {
        // 使用JCache标准的CacheManager
        // 这种方式符合JSR-107标准，便于切换不同的JCache实现
        javax.cache.CachingProvider cachingProvider =
            javax.cache.Caching.getCachingProvider();

        // 获取或创建CacheManager
        // 【重要】cache-manager属性指定了要使用的缓存管理器实现
        javax.cache.CacheManager cacheManager =
            cachingProvider.getCacheManager(
                // 使用classpath下的ehcache.xml配置文件
                getClass().getClassLoader().getResource("ehcache.xml").toURI(),
                // 类加载器，用于加载缓存配置文件
                getClass().getClassLoader()
            );

        // 将JCache的CacheManager适配为Spring的CacheManager
        // 这样就可以在Spring的@Cacheable等注解中使用EhCache
        org.springframework.cache.jcache.JCacheCacheManager jCacheCacheManager =
            new org.springframework.cache.jcache.JCacheCacheManager(cacheManager);

        System.out.println("[CacheConfig] EhCache缓存管理器创建完成");

        return jCacheCacheManager;
    }

    /**
     * 配置简单的ConcurrentHashMap缓存管理器
     *
     * 【适用场景】
     * 1. 开发/测试环境：不需要引入额外依赖
     * 2. 简单应用：不需要复杂的过期策略和淘汰算法
     * 3. 原型开发：快速验证缓存效果
     *
     * 【生产环境不建议使用原因】
     * 1. 不支持过期策略：数据永不过期
     * 2. 不支持淘汰策略：内存可能无限增长
     * 3. 不支持持久化：重启后数据丢失
     * 4. 不支持统计：无法监控缓存命中率
     *
     * @return ConcurrentHashMap缓存管理器
     */
    @Bean
    public CacheManager concurrentHashMapCacheManager() {
        org.springframework.cache.concurrent.ConcurrentMapCacheManager cacheManager =
            new org.springframework.cache.concurrent.ConcurrentMapCacheManager("users", "products");

        System.out.println("[CacheConfig] ConcurrentHashMap缓存管理器创建完成");
        System.out.println("  【警告】这是简单的内存缓存，不支持过期和淘汰策略");

        return cacheManager;
    }

    /**
     * 配置自定义的Key生成器
     *
     * 【Key生成策略】
     * Spring默认使用SimpleKeyGenerator生成key：
     * - 没有参数：key = SimpleKey.EMPTY
     * - 一个参数：key = 参数值
     * - 多个参数：key = SimpleKey(params)
     *
     * 【自定义KeyGenerator的必要性】
     * 1. 当方法参数是对象时，默认的key是对象引用，不同对象即使内容相同也是不同的key
     * 2. 可以根据对象属性（如id）生成key，实现更精细的缓存控制
     * 3. 可以添加前缀，避免不同业务模块的缓存key冲突
     *
     * 【SpEL表达式替代自定义KeyGenerator】
     * 大多数情况下，可以使用@Cacheable(key = "#id")或@Cacheable(key = "#user.id")
     * 这种方式更简洁，不需要自定义KeyGenerator
     *
     * @return 自定义Key生成器
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        // 返回Spring提供的SimpleKeyGenerator
        // 也可以自定义实现KeyGenerator接口
        return new SimpleKeyGenerator() {
            /**
             * 生成缓存key
             *
             * @param target 方法所在对象
             * @param method 被调用的方法
             * @param params 方法参数
             * @return 缓存key
             */
            @Override
            public Object generate(Object target, java.lang.reflect.Method method, Object... params) {
                // 生成格式：类名.方法名:参数1_参数2_参数3
                StringBuilder keyBuilder = new StringBuilder();
                keyBuilder.append(target.getClass().getSimpleName());
                keyBuilder.append(".");
                keyBuilder.append(method.getName());
                keyBuilder.append(":");

                for (int i = 0; i < params.length; i++) {
                    if (i > 0) {
                        keyBuilder.append("_");
                    }
                    // 对于对象参数，使用其hashCode或特定属性
                    if (params[i] != null) {
                        keyBuilder.append(params[i].toString());
                    } else {
                        keyBuilder.append("null");
                    }
                }

                String generatedKey = keyBuilder.toString();
                System.out.println("[KeyGenerator] 生成缓存key: " + generatedKey);
                return generatedKey;
            }
        };
    }

    /**
     * 获取当前配置的缓存管理器列表
     * 【用于监控和调试】
     *
     * @return 缓存管理器列表
     */
    public List<CacheManager> getCacheManagers() {
        return cacheManagers;
    }
}
