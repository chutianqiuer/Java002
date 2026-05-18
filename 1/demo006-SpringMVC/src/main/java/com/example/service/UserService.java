package com.example.service;

import com.example.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务类
 *
 * 本类演示Spring MVC中Service层的作用。
 * Service层负责处理业务逻辑，是Controller和DAO/Repository之间的桥梁。
 *
 * @Service 注解：
 * - 标注此类为Spring的Service组件
 * - Spring会自动扫描并注册为Bean
 * - 默认Bean名称为类名首字母小写（userService）
 *
 * 业务逻辑处理：
 * - 验证用户输入
 * - 调用DAO/Repository进行数据持久化
 * - 处理事务（@Transactional）
 * - 返回业务数据给Controller
 */
@Service
public class UserService {

    /**
     * 模拟用户数据存储
     * 实际项目中应该使用数据库（如MySQL、JPA、MyBatis等）
     */
    private static final Map<Long, User> userMap = new HashMap<>();

    // 静态初始化一些测试数据
    static {
        userMap.put(1L, new User(1L, "张三", "password123", "zhangsan@example.com", 25));
        userMap.put(2L, new User(2L, "李四", "password456", "lisi@example.com", 30));
        userMap.put(3L, new User(3L, "王五", "password789", "wangwu@example.com", 28));
    }

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户对象，如果不存在返回null
     */
    public User getUserById(Long id) {
        return userMap.get(id);
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    public User getUserByUsername(String username) {
        return userMap.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * 创建新用户
     *
     * @param user 用户对象
     * @return 创建成功返回true，失败返回false
     */
    public boolean createUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        if (userMap.containsKey(user.getId())) {
            return false; // 用户已存在
        }
        userMap.put(user.getId(), user);
        return true;
    }

    /**
     * 更新用户信息
     *
     * @param id      用户ID
     * @param user    新的用户信息
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateUser(Long id, User user) {
        if (!userMap.containsKey(id)) {
            return false; // 用户不存在
        }
        user.setId(id); // 确保ID不变
        userMap.put(id, user);
        return true;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteUser(Long id) {
        if (!userMap.containsKey(id)) {
            return false; // 用户不存在
        }
        userMap.remove(id);
        return true;
    }

    /**
     * 用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    public boolean isUsernameExists(String username) {
        return userMap.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
}
