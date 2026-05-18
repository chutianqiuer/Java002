package com.example.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 自定义CacheManager实现 - 演示Spring Cache扩展
 *
 * 【为什么需要自定义CacheManager？】
 *
 * 1. 特殊缓存需求：默认的CacheManager无法满足业务需求
 *    - 需要自定义过期策略
 *    - 需要统计信息
 *    - 需要特殊的存储结构
 *
 * 2. 第三方缓存集成：集成Spring Cache不支持的缓存框架
 *    - 集成Memcached
 *    - 集成Hazelcast
 *    - 集成本地文件缓存
 *
 * 3. 多租户/多数据源：为不同租户/数据源创建独立的缓存
 *    - 每个租户有独立的缓存空间
 *    - 缓存隔离，互不影响
 *
 * 4. 缓存监控和调试：需要拦截缓存操作
 *    - 记录缓存操作日志
 *    - 监控缓存命中率
 *    - 触发缓存预热/清理
 *
 * 【CacheManager接口核心方法】
 *
 * 1. getCache(String name)：获取指定名称的缓存
 * 2. getCacheNames()：获取所有缓存名称
 *
 * 【Cache接口核心方法】
 *
 * 1. get(key)：获取缓存值
 * 2. put(key, value)：写入缓存
 * 3. evict(key)：删除缓存
 * 4. clear()：清空缓存
 * 5. getName()：获取缓存名称
 * 6. getNativeCache()：获取底层缓存实现
 *
 * 【本类演示内容】
 * 1. 基于ConcurrentHashMap的简单缓存实现
 * 2. 带过期时间的缓存
 * 3. 缓存统计功能
 * 4. 线程安全的缓存操作
 * 5. 如何将自定义CacheManager集成到Spring
 */
public class MyCacheManager implements CacheManager {

    /**
     * 缓存存储结构
     * key = 缓存名称
     * value = 缓存对象
     *
     * 【ConcurrentHashMap特点】
     * - 线程安全：多线程并发读写不会数据不一致
     * - 高性能：分段锁机制，减少锁竞争
     * - 不支持null值：get返回null可能是真的不存在，也可能是值就是null
     */
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    /**
     * 缓存配置信息
     * key = 缓存名称
     * value = 缓存配置
     */
    private final Map<String, CacheConfig> cacheConfigs = new ConcurrentHashMap<>();

    /**
     * 默认缓存配置
     */
    private CacheConfig defaultConfig = new CacheConfig();

    /**
     * 获取所有缓存名称
     *
     * @return 缓存名称集合
     */
    @Override
    public Collection<String> getCacheNames() {
        // 返回不可修改的集合，防止外部修改
        return Collections.unmodifiableSet(cacheMap.keySet());
    }

    /**
     * 获取指定名称的缓存
     *
     * 【缓存获取策略】
     * 1. 先从缓存Map中查找
     * 2. 存在则直接返回
     * 3. 不存在则创建新的缓存
     * 4. 将新缓存存入Map并返回
     *
     * 【懒加载模式】
     * 这种模式属于懒加载，只有第一次访问某个缓存时才会创建
     * 优点：启动快，资源按需分配
     * 缺点：第一次访问会有一定延迟
     *
     * 【预加载模式】
     * 在初始化时就创建所有缓存
     * 优点：首次访问无延迟
     * 缺点：启动慢，可能浪费资源
     *
     * @param name 缓存名称
     * @return 缓存对象
     */
    @Override
    public Cache getCache(String name) {
        // 1. 先从缓存Map中查找
        Cache cache = cacheMap.get(name);

        if (cache != null) {
            System.out.println("[MyCacheManager] 获取缓存 '" + name + "'（已存在）");
            return cache;
        }

        // 2. 不存在则创建新的缓存
        System.out.println("[MyCacheManager] 创建新缓存 '" + name + "'");

        // 获取该缓存的配置，如果没有则使用默认配置
        CacheConfig config = cacheConfigs.getOrDefault(name, defaultConfig);

        // 3. 创建缓存实例
        MyCache newCache = new MyCache(name, config);

        // 4. 存入Map并返回
        // 使用putIfAbsent保证线程安全，避免重复创建
        Cache existingCache = cacheMap.putIfAbsent(name, newCache);

        // 如果其他线程已经创建了同名的缓存，返回已有的缓存
        return existingCache != null ? existingCache : newCache;
    }

    /**
     * 注册一个缓存（预加载模式）
     *
     * 【使用场景】
     * - 在系统启动时预先创建缓存
     * - 确保关键缓存一定存在
     * - 配合@Cacheable的earlyPut属性使用
     *
     * @param name   缓存名称
     * @param config 缓存配置
     */
    public void registerCache(String name, CacheConfig config) {
        CacheConfig existingConfig = cacheConfigs.putIfAbsent(name, config);
        if (existingConfig == null) {
            MyCache cache = new MyCache(name, config);
            cacheMap.put(name, cache);
            System.out.println("[MyCacheManager] 注册缓存 '" + name + "'，过期时间=" + config.getExpireAfterWriteSeconds() + "秒");
        }
    }

    /**
     * 销毁所有缓存
     *
     * 【使用场景】
     * - 系统关闭时清理资源
     * - 测试环境重置状态
     * - 缓存数据过期或不再需要时清理
     */
    public void destroy() {
        System.out.println("[MyCacheManager] 销毁所有缓存，共 " + cacheMap.size() + " 个");
        for (Cache cache : cacheMap.values()) {
            cache.clear();
        }
        cacheMap.clear();
        cacheConfigs.clear();
    }

    /**
     * 获取缓存统计信息
     *
     * @return 统计信息
     */
    public CacheStatistics getStatistics() {
        long totalHits = 0;
        long totalMisses = 0;
        long totalPuts = 0;

        for (Cache cache : cacheMap.values()) {
            if (cache instanceof MyCache) {
                MyCache myCache = (MyCache) cache;
                totalHits += myCache.getHits();
                totalMisses += myCache.getMisses();
                totalPuts += myCache.getPuts();
            }
        }

        return new CacheStatistics(totalHits, totalMisses, totalPuts);
    }

    /**
     * 内部类：自定义Cache实现
     *
     * 【设计要点】
     * 1. 包装ConcurrentHashMap存储数据
     * 2. 存储CacheEntry（包含值和过期时间）
     * 3. 后台线程定期清理过期缓存
     * 4. 提供统计信息
     */
    static class MyCache implements Cache {

        /**
         * 缓存名称
         */
        private final String name;

        /**
         * 缓存配置
         */
        private final CacheConfig config;

        /**
         * 缓存存储：key -> CacheEntry
         */
        private final Map<Object, CacheEntry> store = new ConcurrentHashMap<>();

        /**
         * 统计信息
         */
        private volatile long hits = 0;
        private volatile long misses = 0;
        private volatile long puts = 0;

        /**
         * 后台清理线程标志
         */
        private volatile boolean cleanupRunning = false;

        public MyCache(String name, CacheConfig config) {
            this.name = name;
            this.config = config;
            // 启动后台清理线程
            startCleanupTask();
        }

        /**
         * 启动后台清理过期缓存的任务
         *
         * 【为什么要后台清理？】
         * - ConcurrentHashMap不会自动清理过期数据
         * - 需要定期扫描并删除过期缓存
         * - 避免内存泄漏
         *
         * 【清理策略】
         * - 每分钟执行一次清理
         * - 只清理过期的缓存
         * - 避免清理过程影响正常缓存操作
         */
        private void startCleanupTask() {
            if (cleanupRunning) {
                return;
            }

            cleanupRunning = true;

            Thread cleanupThread = new Thread(() -> {
                System.out.println("[MyCache] 启动过期缓存清理线程，缓存名称=" + name);

                while (cleanupRunning) {
                    try {
                        // 每60秒执行一次清理
                        Thread.sleep(60000);
                        cleanupExpiredEntries();
                    } catch (InterruptedException e) {
                        System.out.println("[MyCache] 清理线程被中断");
                        break;
                    }
                }

                System.out.println("[MyCache] 过期缓存清理线程退出，缓存名称=" + name);
            });

            cleanupThread.setDaemon(true); // 设置为守护线程，不阻止JVM退出
            cleanupThread.start();
        }

        /**
         * 清理过期的缓存条目
         */
        private void cleanupExpiredEntries() {
            long now = System.currentTimeMillis();
            int removedCount = 0;

            for (Map.Entry<Object, CacheEntry> entry : store.entrySet()) {
                if (entry.getValue().isExpired(now)) {
                    if (store.remove(entry.getKey(), entry.getValue())) {
                        removedCount++;
                    }
                }
            }

            if (removedCount > 0) {
                System.out.println("[MyCache] 清理过期缓存，缓存名称=" + name + "，清理数量=" + removedCount);
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getNativeCache() {
            return store;
        }

        /**
         * 获取缓存值
         *
         * 【执行流程】
         * 1. 根据key查找缓存条目
         * 2. 检查是否过期
         * 3. 过期则删除，返回null
         * 4. 未过期则更新统计信息，返回值
         *
         * @param key 缓存key
         * @return 缓存值，如果不存在或已过期返回null
         */
        @Override
        public ValueWrapper get(Object key) {
            CacheEntry entry = store.get(key);

            if (entry == null) {
                misses++;
                System.out.println("[MyCache] 缓存未命中，key=" + key + "，缓存名称=" + name);
                return null;
            }

            long now = System.currentTimeMillis();

            if (entry.isExpired(now)) {
                // 已过期，删除并返回null
                store.remove(key);
                misses++;
                System.out.println("[MyCache] 缓存已过期，key=" + key + "，缓存名称=" + name);
                return null;
            }

            hits++;
            System.out.println("[MyCache] 缓存命中，key=" + key + "，缓存名称=" + name);
            return () -> entry.getValue();
        }

        /**
         * 获取缓存值（泛型版本）
         *
         * @param key      缓存key
         * @param type 返回值类型
         * @return 缓存值
         */
        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(Object key, Class<T> type) {
            ValueWrapper wrapper = get(key);
            if (wrapper == null) {
                return null;
            }
            Object value = wrapper.get();
            if (value != null && type != null && !type.isInstance(value)) {
                throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
            }
            return (T) value;
        }

        /**
         * 获取缓存值，如果不存在则调用loader加载
         *
         * 【Spring 5.1新增功能】
         * 这个方法提供了更便捷的缓存读取方式：
         * - 如果缓存存在，直接返回
         * - 如果缓存不存在，调用loader加载数据
         * - 将加载的数据存入缓存并返回
         *
         * @param key      缓存key
         * @param loader   数据加载器
         * @param <T>      返回值类型
         * @return 缓存值
         */
        @Override
        public <T> T get(Object key, java.util.concurrent.Callable<T> loader) {
            T value = (T) get(key);
            if (value == null && loader != null) {
                try {
                    value = loader.call();
                    put(key, value);
                } catch (Exception e) {
                    throw new ValueRetrievalException(key, loader, e);
                }
            }
            return value;
        }

        /**
         * 写入缓存
         *
         * @param key   缓存key
         * @param value 缓存值
         */
        @Override
        public void put(Object key, Object value) {
            // 计算过期时间
            long expireTime = config.getExpireAfterWriteSeconds() > 0
                ? System.currentTimeMillis() + config.getExpireAfterWriteSeconds() * 1000
                : 0; // 0表示永不过期

            CacheEntry entry = new CacheEntry(value, expireTime);
            store.put(key, entry);
            puts++;
            System.out.println("[MyCache] 写入缓存，key=" + key + "，过期时间=" + expireTime + "，缓存名称=" + name);
        }

        /**
         * 删除缓存
         *
         * @param key 缓存key
         */
        @Override
        public void evict(Object key) {
            store.remove(key);
            System.out.println("[MyCache] 删除缓存，key=" + key + "，缓存名称=" + name);
        }

        /**
         * 清空缓存
         *
         * 【注意】
         * clear()会删除所有缓存条目
         * 在多级缓存架构中，需要按顺序清空各级缓存
         */
        @Override
        public void clear() {
            store.clear();
            System.out.println("[MyCache] 清空缓存，缓存名称=" + name);
        }

        /**
         * 获取命中次数
         */
        public long getHits() {
            return hits;
        }

        /**
         * 获取未命中次数
         */
        public long getMisses() {
            return misses;
        }

        /**
         * 获取写入次数
         */
        public long getPuts() {
            return puts;
        }

        /**
         * 获取命中率
         */
        public double getHitRate() {
            long total = hits + misses;
            return total > 0 ? (double) hits / total : 0.0;
        }
    }

    /**
     * 缓存条目封装类
     * 包含值和过期时间
     */
    static class CacheEntry {
        private final Object value;
        private final long expireTime; // 0表示永不过期

        public CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        public Object getValue() {
            return value;
        }

        public long getExpireTime() {
            return expireTime;
        }

        /**
         * 判断是否已过期
         *
         * @param now 当前时间戳
         * @return true表示已过期
         */
        public boolean isExpired(long now) {
            // expireTime为0表示永不过期
            return expireTime > 0 && now > expireTime;
        }
    }

    /**
     * 缓存配置类
     */
    static class CacheConfig {
        /**
         * 写入后过期时间（秒）
         * 0表示永不过期
         */
        private long expireAfterWriteSeconds = 3600; // 默认1小时

        /**
         * 访问后过期时间（秒）
         * 0表示永不过期
         */
        private long expireAfterAccessSeconds = 0;

        public long getExpireAfterWriteSeconds() {
            return expireAfterWriteSeconds;
        }

        public void setExpireAfterWriteSeconds(long expireAfterWriteSeconds) {
            this.expireAfterWriteSeconds = expireAfterWriteSeconds;
        }

        public long getExpireAfterAccessSeconds() {
            return expireAfterAccessSeconds;
        }

        public void setExpireAfterAccessSeconds(long expireAfterAccessSeconds) {
            this.expireAfterAccessSeconds = expireAfterAccessSeconds;
        }
    }

    /**
     * 缓存统计信息
     */
    static class CacheStatistics {
        private final long hits;
        private final long misses;
        private final long puts;

        public CacheStatistics(long hits, long misses, long puts) {
            this.hits = hits;
            this.misses = misses;
            this.puts = puts;
        }

        public long getHits() {
            return hits;
        }

        public long getMisses() {
            return misses;
        }

        public long getPuts() {
            return puts;
        }

        public double getHitRate() {
            long total = hits + misses;
            return total > 0 ? (double) hits / total : 0.0;
        }

        @Override
        public String toString() {
            return "CacheStatistics{" +
                    "hits=" + hits +
                    ", misses=" + misses +
                    ", puts=" + puts +
                    ", hitRate=" + String.format("%.2f%%", getHitRate() * 100) +
                    '}';
        }
    }

    /**
     * ValueRetrievalException异常类
     */
    static class ValueRetrievalException extends RuntimeException {
        private final Object key;
        private final java.util.concurrent.Callable<?> loader;

        public ValueRetrievalException(Object key, java.util.concurrent.Callable<?> loader, Throwable cause) {
            super("Value for key '" + key + "' could not be loaded using loader", cause);
            this.key = key;
            this.loader = loader;
        }

        public Object getKey() {
            return key;
        }

        public java.util.concurrent.Callable<?> getLoader() {
            return loader;
        }
    }
}
