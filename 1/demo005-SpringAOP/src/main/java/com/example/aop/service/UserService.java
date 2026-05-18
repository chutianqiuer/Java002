package com.example.aop.service;

import com.example.model.User;

/**
 * 用户服务类 - 演示AOP的各种应用场景
 *
 * AOP核心概念对应关系：
 *
 * 1. Join Point（连接点）
 *    - 定义：程序执行过程中可以被拦截的点
 *    - 在Spring AOP中，特指方法被调用的执行点
 *    - 本类中每个public方法都是连接点
 *
 * 2. Pointcut（切入点）
 *    - 定义：用于匹配连接点的表达式
 *    - 作用：指明哪些连接点需要被增强
 *    - 举例：execution(* com.example.aop.service.*.*(..))
 * *            匹配com.example.aop.service包下所有类的所有方法
 *
 * 3. Advice（通知/增强）
 *    - 定义：切面在切入点执行的额外代码
 *    - 五种类型：@Before、@After、@AfterReturning、@AfterThrowing、@Around
 *    - 作用：在目标方法执行前、后、返回时、异常时执行额外逻辑
 *
 * 4. Aspect（切面）
 *    - 定义：切入点 + 通知的组合
 *    - 包含：切点表达式（where）+ 通知逻辑（what）
 *    - 本例中：LoggingAspect、PerformanceAspect、ValidationAspect都是切面
 *
 * 5. Weaving（织入）
 *    - 定义：将切面应用到目标对象的过程
 *    - 时期：编译期、类加载期、运行期
 *    - Spring AOP采用运行期代理的方式实现织入
 *
 * 6. Proxy（代理）
 *    - 定义：为目标对象创建的代理对象
 *    - 类型：JDK动态代理（接口）、CGLIB代理（类）
 *    - 用户调用实际经过代理，代理控制方法执行和增强逻辑
 *
 * 7. Target（目标对象）
 *    - 定义：被代理的原始对象
 *    - 也称为：被通知对象（advised object）
 */
public class UserService {

    /**
     * 模拟用户数据存储
     */
    private static User currentUser = new User(1L, "testUser", "test@example.com");

    /**
     * 根据ID获取用户 - 演示@Before和@AfterReturning
     *
     * 切入点匹配：本方法会被LoggingAspect拦截
     * 原因：切入点表达式匹配了save开头的方法名
     *
     * 方法执行流程（无异常情况）：
     * 1. @Before - 前置通知执行（日志记录）
     * 2. 目标方法 - findById执行
     * 3. @AfterReturning - 返回通知执行（日志记录）
     * 4. @After - 后置通知执行（释放资源等）
     *
     * @param id 用户ID
     * @return 用户对象，如果未找到返回null
     */
    public User findById(Long id) {
        System.out.println("[UserService] 执行findById方法，参数：id=" + id);

        // 模拟数据库查询
        if (id.equals(currentUser.getId())) {
            return currentUser;
        }
        return null;
    }

    /**
     * 保存用户 - 演示@Before、@After、@AfterThrowing
     *
     * 方法执行流程（正常情况）：
     * 1. @Before - 前置通知（参数验证、日志）
     * 2. 目标方法 - saveUser执行
     * 3. @AfterReturning - 返回通知（成功日志）
     * 4. @After - 后置通知（资源释放）
     *
     * 方法执行流程（异常情况）：
     * 1. @Before - 前置通知
     * 2. 目标方法 - saveUser执行，抛出异常
     * 3. @AfterThrowing - 异常通知（异常日志、补偿逻辑）
     * 4. @After - 后置通知（资源释放）
     *
     * @param user 要保存的用户对象
     * @return 保存成功返回true，失败返回false
     */
    public boolean saveUser(User user) {
        System.out.println("[UserService] 执行saveUser方法");

        // 模拟数据库保存操作
        if (user == null) {
            throw new IllegalArgumentException("用户对象不能为空");
        }

        currentUser = user;
        System.out.println("[UserService] 用户保存成功：" + user);
        return true;
    }

    /**
     * 更新用户 - 演示@Around环绕通知
     *
     * @Around是最强大的通知类型，它可以：
     * 1. 在目标方法调用前执行自己的逻辑
     * 2. 决定是否调用目标方法（通过proceed()）
     * 3. 在目标方法调用后执行自己的逻辑
     * 4. 完全控制目标方法的执行流程
     *
     * @param user 要更新的用户对象
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateUser(User user) {
        System.out.println("[UserService] 执行updateUser方法");

        // 模拟数据库更新操作
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户对象或ID不能为空");
        }

        currentUser = user;
        System.out.println("[UserService] 用户更新成功：" + user);
        return true;
    }

    /**
     * 删除用户 - 演示异常处理
     *
     * @param id 要删除的用户ID
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteUser(Long id) {
        System.out.println("[UserService] 执行deleteUser方法，参数：id=" + id);

        // 模拟数据库删除操作
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (!id.equals(currentUser.getId())) {
            throw new RuntimeException("用户不存在，ID：" + id);
        }

        currentUser = null;
        System.out.println("[UserService] 用户删除成功，ID：" + id);
        return true;
    }

    /**
     * 验证用户 - 演示参数匹配
     *
     * 切入点表达式可以通过args()匹配特定参数
     * 例如：args(Long) 匹配只接收一个Long类型参数的方法
     *
     * @param id 用户ID
     * @return 验证结果
     */
    public boolean validateUser(Long id) {
        System.out.println("[UserService] 执行validateUser方法，参数：id=" + id);
        return id != null && id.equals(currentUser.getId());
    }

    /**
     * 批量操作演示方法 - 用于展示within()切入点匹配
     * within()用于匹配特定类型的所有方法
     *
     * within(com.example.aop.service.UserService)
     * 会匹配UserService类的所有方法
     */
    public void batchOperation() {
        System.out.println("[UserService] 执行batchOperation批量操作");
    }
}
