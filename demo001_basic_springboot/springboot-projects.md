# SpringBoot 项目设计总结

## 1. 表结构设计

```
┌─────────────────────────────────────────────────────────────┐
│                        系统管理模块                           │
├─────────────────────────────────────────────────────────────┤
│  sys_user        - 用户表                                    │
│  sys_role        - 角色表                                    │
│  sys_permission  - 权限表（菜单+按钮）                          │
│  sys_user_role   - 用户-角色关联表                            │
│  sys_role_permission - 角色-权限关联表                          │
│  sys_file        - 文件表                                     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                        业务模块                              │
├─────────────────────────────────────────────────────────────┤
│  prd_category    - 商品分类表                                │
│  prd_product     - 商品表                                    │
│  ord_order       - 订单表                                    │
│  ord_order_item  - 订单明细表                                 │
│  ord_payment     - 支付表                                    │
└─────────────────────────────────────────────────────────────┘
```

**核心表设计规范**：

```sql
-- 通用基座字段（每张表都有）
id, create_time, update_time, create_by, update_by, deleted, version

-- 用户表示例
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status INT DEFAULT 1,        -- 0:禁用 1:启用
    sex INT DEFAULT 0,          -- 0:未知 1:男 2:女
    create_time DATETIME,
    update_time DATETIME,
    deleted INT DEFAULT 0,
    version INT DEFAULT 0
);

-- 树形结构（菜单/分类）
parent_id BIGINT DEFAULT 0,     -- 父级ID，0表示顶级
sort INT DEFAULT 0,            -- 排序号
```

---

## 2. 接口分层架构

```
┌──────────────────────────────────────────────────────────────┐
│                      Controller 层                            │
│  @RestController                                            │
│  处理请求参数校验、调用Service、返回Result                      │
└──────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────┐
│                       Service 层                              │
│  @Service                                                   │
│  业务逻辑处理、事务管理、调用Mapper                            │
└──────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────┐
│                      Mapper/Repository 层                     │
│  @Mapper / MyBatis Plus BaseMapper                          │
│  数据访问、SQL编写                                            │
└──────────────────────────────────────────────────────────────┘
                              ↓
                         Database
```

**分层职责**：

| 层级 | 职责 | 注解 |
|------|------|------|
| Controller | 请求参数校验、参数转换、调用Service、异常捕获 | `@RestController` `@RequestMapping` |
| Service | 业务逻辑、事务控制、调用Mapper | `@Service` `@Transactional` |
| Mapper | 数据CURD、SQL编写 | `@Mapper` `BaseMapper<T>` |

---

## 3. 权限设计

**基于RBAC模型（Role-Based Access Control）**：

```
                    ┌─────────────┐
                    │    用户      │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  用户-角色    │
                    │  关联表      │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │    角色      │◄────── 角色权限关联表 ──────┐
                    └──────┬──────┘                            │
                           │                                   │
                    ┌──────▼──────┐                            │
                    │   权限菜单    │◄──────────────────────────┘
                    └─────────────┘
```

**权限类型**：

```sql
-- type: 1=菜单 2=按钮
-- 菜单权限控制页面访问
-- 按钮权限控制操作（如：product:add, product:edit, product:delete）
```

**实现方式**：

```java
// JWT Token 包含用户ID和基本信息
// 请求头携带 Token: Authorization: Bearer <token>
// 过滤器 JwtAuthenticationFilter 解析Token，存入SecurityContext
// Spring Security 根据接口配置决定是否放行
```

---

## 4. 前后端对接

**接口规范**：

```
请求格式：
  Headers: Content-Type: application/json
          Authorization: Bearer <token>
  Body: JSON格式

响应格式：
{
  "code": 200,           // 业务状态码
  "message": "操作成功",   // 提示信息
  "data": { ... }        // 返回数据
}
```

**认证流程**：

```
前端                        后端
  │                          │
  ├─── 登录 POST /auth/login ──► Validate ──► 返回JWT Token
  │◄──── { token, ... } ──────┤
  │                          │
  ├─── 请求 + Token ─────────► Validate Token ──► 业务处理
  │◄──── Response ────────────┤
```

---

## 5. 公共返回格式封装

```java
// 统一返回格式
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;      // 状态码：200成功，500失败
    private String message;    // 消息
    private T data;           // 数据

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
```

**分页返回**：

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class PageVO<T> extends BaseVO {
    private Long total;        // 总记录数
    private List<T> records;   // 数据列表
    private Integer page;       // 当前页
    private Integer pageSize;  // 每页条数
    private Integer totalPages;// 总页数
}
```

---

## 6. 异常处理

**自定义业务异常**：

```java
public class BusinessException extends RuntimeException {
    private Integer code = 500;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
```

**全局异常处理器**：

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError()
                            .getDefaultMessage();
        return Result.error(400, message);
    }

    // 通用异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常");
    }
}
```

---

## 7. 分页处理

**DTO**：

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class PageDTO extends BaseDTO {
    private Integer page = 1;      // 默认第1页
    private Integer pageSize = 10; // 默认10条
}
```

**查询条件DTO**：

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDTO extends PageDTO {
    private String name;       // 商品名称（模糊搜索）
    private Long categoryId;   // 分类ID（精确查询）
    private Integer status;    // 状态（精确查询）
}
```

**Service实现**：

```java
@Override
public PageVO<ProductVO> getPage(ProductDTO productDTO) {
    LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

    // 动态条件
    if (productDTO.getName() != null) {
        wrapper.like(Product::getName, productDTO.getName());
    }
    if (productDTO.getCategoryId() != null) {
        wrapper.eq(Product::getCategoryId, productDTO.getCategoryId());
    }

    wrapper.orderByDesc(Product::getCreateTime);

    // MyBatis Plus 分页
    Page<Product> page = new Page<>(
        productDTO.getPage(),    // 当前页
        productDTO.getPageSize() // 每页大小
    );
    Page<Product> result = productMapper.selectPage(page, wrapper);

    // 封装返回
    PageVO<ProductVO> pageVO = new PageVO<>();
    pageVO.setTotal(result.getTotal());
    pageVO.setRecords(BeanCopyUtils.copyBeanList(result.getRecords(), ProductVO.class));
    pageVO.setPage((int) result.getCurrent());
    pageVO.setPageSize((int) result.getSize());
    pageVO.setTotalPages((int) result.getPages());
    return pageVO;
}
```

---

## 8. 字典/菜单/角色/用户设计

### 8.1 用户表 (sys_user)

```sql
id, username, password, real_name, email, phone, avatar, status, sex
```

### 8.2 角色表 (sys_role)

```sql
id, code(唯一), name, description, status, sort
-- 示例: SUPER_ADMIN, 超级管理员, 拥有所有权限
```

### 8.3 权限/菜单表 (sys_permission)

```sql
id, code, name, path, component, type(1菜单/2按钮), parent_id, sort, icon, status
```

**示例数据**：

```
id  code                 name         path              component      type  parent_id  sort
1   system              系统管理      /system           Layout         1     0          1
2   system:user         用户管理      /system/user      system/User    1     1          1
3   system:role         角色管理      /system/role      system/Role    1     1          2
4   product:add         添加商品      NULL              NULL           2     5          2
5   product:edit        编辑商品      NULL              NULL           2     5          3
```

### 8.4 关联表

```sql
-- 用户有哪些角色
sys_user_role: user_id, role_id

-- 角色有哪些权限
sys_role_permission: role_id, permission_id
```

### 8.5 数据字典设计

```sql
-- 可以用枚举或配置表
-- 方案1: 枚举类（推荐，性能好）
public enum OrderStatus {
    PENDING_PAYMENT("待支付"),
    PAID("已支付"),
    SHIPPED("已发货"),
    COMPLETED("已完成"),
    CANCELLED("已取消");
}

-- 方案2: 字典表（适合动态配置）
sys_dict: id, type, code, name, value, sort, status
```

**前端动态渲染菜单**：

```java
// 根据用户角色获取菜单树
List<MenuVO> getMenusByUserId(Long userId);

// 返回结构
[
  {
    "id": 1,
    "name": "系统管理",
    "path": "/system",
    "children": [
      {"id": 2, "name": "用户管理", "path": "/system/user"},
      {"id": 3, "name": "角色管理", "path": "/system/role"}
    ]
  }
]
```

---

## 项目结构总览

```
springboot-projects/
├── pom.xml                    # 父POM
├── docker-compose.yml          # MySQL + Redis
├── sql/schema.sql             # 建表脚本
│
├── common/                    # 公共模块
│   └── src/main/java/com/example/common/
│       ├── entity/            # 实体类（继承BaseEntity）
│       ├── dto/               # 数据传输对象（PageDTO, *DTO）
│       ├── vo/                # 视图对象（PageVO, *VO, Result）
│       ├── enums/             # 枚举（OrderStatus, YesOrNo）
│       ├── utils/             # 工具类
│       └── exception/         # 异常定义和全局处理器
│
├── mapper/                    # 数据访问层
│   └── src/main/java/com/example/mapper/
│       └── *Mapper.java       # MyBatis Plus Mapper接口
│
├── service/                   # 业务逻辑层
│   └── src/main/java/com/example/service/
│       ├── *Service.java      # Service接口
│       └── impl/*ServiceImpl.java  # Service实现
│
├── web/                       # 前台API模块（端口8080）
│   └── src/main/java/com/example/web/
│       ├── controller/        # REST控制器 + Swagger注解
│       ├── config/            # SecurityConfig, OpenApiConfig
│       └── security/           # JwtAuthenticationFilter
│
└── admin/                     # 后台管理模块（端口8081）
    └── src/main/java/com/example/admin/
        ├── controller/        # Admin控制器
        ├── dto/               # Admin专用DTO
        └── service/            # Admin专用Service
```

---

## 快速启动

```bash
# 1. 启动数据库
cd springboot-projects
docker compose up -d

# 2. 初始化数据库
docker exec -i springboot-mysql mysql -uroot -proot123 < sql/schema.sql

# 3. 构建项目
mvn clean package -DskipTests

# 4. 运行前台服务 (端口8080)
java -jar web/target/web-1.0.0.jar

# 5. 运行后台管理服务 (端口8081)
java -jar admin/target/admin-1.0.0.jar
```

## API 文档地址

| 文档 | 地址 |
|------|------|
| Web Swagger UI | http://localhost:8080/swagger-ui.html |
| Web API JSON | http://localhost:8080/api-docs |
| Admin Swagger UI | http://localhost:8081/swagger-ui.html |
| Admin API JSON | http://localhost:8081/api-docs |

## 默认账号

```
用户名: admin
密码: admin123
```
