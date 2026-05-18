# Spring Validation 示例项目

本项目是 Spring Validation（数据校验）的深入学习示例，展示了 Spring Framework 中完整的数据校验能力。

## 项目概述

Spring Validation 是 Spring Framework 提供的数据校验框架，基于 JSR-303/JSR-380（Bean Validation）规范。它允许开发者在实体类上使用注解来声明校验规则，无需在业务代码中编写大量的 if-else 校验逻辑。

## 技术栈

- **Spring Boot**: 2.7.14
- **Spring Framework**: 5.3.x
- **Java**: 8+
- **Maven**: 3.6+
- **Validation**: JSR-303/JSR-380

## 快速开始

### 1. 编译项目

```bash
cd demo008-SpringValidation
mvn clean compile
```

### 2. 运行项目

```bash
mvn spring-boot:run
```

或者打包后运行：

```bash
mvn clean package
java -jar target/demo008-SpringValidation-1.0.0.jar
```

### 3. 访问项目

启动后访问：http://localhost:8080

## 项目结构

```
demo008-SpringValidation/
├── pom.xml                                      # Maven 配置文件
├── src/main/java/com/example/
│   ├── Demo008Application.java                  # Spring Boot 启动类
│   ├── controller/
│   │   ├── UserController.java                  # 用户控制器
│   │   └── GlobalExceptionHandler.java          # 全局异常处理器
│   ├── model/
│   │   ├── User.java                            # 用户实体（基础校验注解演示）
│   │   ├── Address.java                         # 地址实体（嵌套校验演示）
│   │   └── RegisterRequest.java                 # 注册请求（分组校验演示）
│   ├── validator/
│   │   ├── CustomValidator.java                 # 自定义校验注解
│   │   └── CustomValidatorImpl.java              # 自定义校验器实现
│   └── dto/
│       └── ApiResponse.java                     # 统一响应对象
└── src/main/resources/
    └── messages.properties                      # 国际化错误消息配置
```

## 核心功能演示

### 1. JSR-303/JSR-380 校验注解

#### 不能为空校验

| 注解 | 作用 | 适用类型 |
|------|------|----------|
| `@NotNull` | 不能为 null | 所有类型 |
| `@NotBlank` | 不能为 null、空字符串、空白字符 | CharSequence |
| `@NotEmpty` | 不能为 null 且不能为空 | Collection、Map、数组、CharSequence |

**示例请求**：
```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "password": "",
    "age": -1
  }'
```

**错误响应**：
```json
{
  "code": 400,
  "message": "参数校验失败",
  "errors": [
    {"field": "username", "message": "用户名不能为空"},
    {"field": "password", "message": "密码不能为空"},
    {"field": "age", "message": "年龄不能小于0岁"}
  ]
}
```

#### 长度校验

| 注解 | 作用 | 适用类型 |
|------|------|----------|
| `@Size(min, max)` | 长度/大小在指定范围内 | Collection、Map、数组、CharSequence |
| `@Length(min, max)` | Hibernate 特有，字符串长度限制 | CharSequence |

#### 数值范围校验

| 注解 | 作用 | 适用类型 |
|------|------|----------|
| `@Min(value)` | 大于或等于指定值 | 数值类型 |
| `@Max(value)` | 小于或等于指定值 | 数值类型 |
| `@DecimalMin(value)` | 大于或等于指定值（支持小数） | BigDecimal、String |
| `@DecimalMax(value)` | 小于或等于指定值（支持小数） | BigDecimal、String |
| `@Positive` | 正数 | 数值类型 |
| `@PositiveOrZero` | 正数或零 | 数值类型 |
| `@Negative` | 负数 | 数值类型 |
| `@NegativeOrZero` | 负数或零 | 数值类型 |
| `@Digits(integer, fraction)` | 整数和小数部分位数限制 | 数值类型 |

**示例请求**：
```bash
curl -X POST "http://localhost:8080/api/user/balance?amount=0.001&maxAmount=9999999&points=-1"
```

#### 格式校验

| 注解 | 作用 | 适用类型 |
|------|------|----------|
| `@Email` | 邮箱格式 | CharSequence |
| `@Pattern(regexp)` | 正则表达式 | CharSequence |
| `@Digits(integer, fraction)` | 数字格式（整数和小数位数） | 数值类型 |

**示例请求**：
```bash
curl -X GET "http://localhost:8080/api/user/email/user@example.com"
```

#### 日期校验

| 注解 | 作用 | 适用类型 |
|------|------|----------|
| `@Past` | 过去的日期 | Date、LocalDate、LocalDateTime |
| `@Future` | 未来的日期 | Date、LocalDate、LocalDateTime |
| `@PastOrPresent` | 过去或现在的日期 | 日期类型 |
| `@FutureOrPresent` | 未来或现在的日期 | 日期类型 |

**示例请求**：
```bash
curl -X POST "http://localhost:8080/api/user/birthday?birthDate=2030-01-01"
```

### 2. @Validated 和 @Valid 的区别

| 特性 | @Validated | @Valid |
|------|------------|--------|
| 来源 | Spring | JSR-303 |
| 类级别支持 | 支持 | 不支持 |
| 分组校验 | 支持 | 不支持 |
| 嵌套校验 | 不支持 | 支持 |

**@Valid 用于嵌套对象校验**：
```java
@PostMapping("/register")
public ApiResponse<User> register(@Valid @RequestBody User user) {
    // user 对象会被校验
    // 如果 user.address 不为 null，Address 对象也会被递归校验
}
```

**@Validated 用于分组校验**：
```java
@PostMapping("/create")
public ApiResponse<RegisterRequest> create(
    @Validated(RegisterRequest.CreateGroup.class) @RequestBody RegisterRequest request) {
    // 只校验 CreateGroup 分组的字段
}
```

### 3. 分组校验

分组校验允许同一个实体类在不同场景下校验不同的字段。

**定义分组接口**：
```java
public interface CreateGroup extends Default {}
public interface UpdateGroup extends Default {}
```

**在注解中指定分组**：
```java
@NotBlank(message = "密码不能为空", groups = {CreateGroup.class})
@Size(min = 6, max = 20, message = "密码长度必须在6-20位之间", groups = {CreateGroup.class})
private String password;
```

**校验时指定分组**：
```bash
# 创建时校验（包含密码）
curl -X POST http://localhost:8080/api/user/create \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "123"}'

# 更新时校验（不校验密码）
curl -X POST http://localhost:8080/api/user/update \
  -H "Content-Type: application/json" \
  -d '{"username": "test"}'
```

### 4. 自定义校验器

**步骤1：创建校验注解**
```java
@Constraint(validatedBy = {CustomValidatorImpl.class})
public @interface CustomValidator {
    String message() default "校验失败";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean required() default true;
    int min() default 0;
    int max() default Integer.MAX_VALUE;
}
```

**步骤2：实现校验器**
```java
public class CustomValidatorImpl implements ConstraintValidator<CustomValidator, String> {
    @Override
    public void initialize(CustomValidator constraintAnnotation) {
        // 初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 校验逻辑
        return true/false;
    }
}
```

**步骤3：使用自定义校验器**
```java
@CustomValidator(required = true, min = 3, max = 20)
private String field;
```

**示例请求**：
```bash
curl -X POST http://localhost:8080/api/user/validate \
  -H "Content-Type: application/json" \
  -d '"test"'
```

### 5. 嵌套对象校验

当对象包含嵌套对象时，使用 `@Valid` 标记嵌套对象进行递归校验。

```java
public class User {
    @Valid
    private Address address;  // Address 对象也会被校验
}
```

**示例请求**（触发嵌套对象校验失败）：
```bash
curl -X POST http://localhost:8080/api/user/full \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "address": {
      "province": "",
      "city": "北京",
      "district": "昌平",
      "detailAddress": "某地"
    }
  }'
```

### 6. 校验错误消息国际化

在 `messages.properties` 中配置错误消息：

```properties
javax.validation.constraints.NotNull.message=此字段不能为空
javax.validation.constraints.Size.message=长度必须在{min}到{max}个字符之间
javax.validation.constraints.Email.message=邮箱格式不正确
```

## API 端点一览

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/register | 用户注册（综合校验） |
| POST | /api/user/create | 创建用户（分组校验） |
| POST | /api/user/update | 更新用户（分组校验） |
| GET | /api/user/{id} | 获取用户（路径参数校验） |
| GET | /api/user/email/{email} | 邮箱格式校验 |
| GET | /api/user/phone/{phone} | 手机号格式校验 |
| POST | /api/user/appointment | 预约日期校验 |
| POST | /api/user/validate | 自定义校验器演示 |
| POST | /api/user/batch | 批量操作（列表校验） |
| POST | /api/user/balance | 转账金额校验 |
| POST | /api/user/price | 价格格式校验 |
| POST | /api/user/agree | 协议确认校验 |
| POST | /api/user/full | 完整用户信息（嵌套校验） |
| GET | /api/user/verify/{phone} | 验证手机号 |
| POST | /api/user/birthday | 设置生日 |
| GET | /api/user/period | 查询时间段 |

## 测试校验失败

所有校验失败都会返回统一格式的错误响应：

```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": null,
  "errors": [
    {
      "field": "username",
      "fieldName": "username",
      "message": "用户名不能为空",
      "rejectedValue": "",
      "errorCode": "NotBlank"
    }
  ],
  "timestamp": 1699999999999
}
```

## 常见问题

### Q: 为什么不使用 @Validated 在类级别？
A: @Validated 在类级别不支持分组校验，但会触发所有 @Valid 方法的校验。如果需要分组校验，应在方法参数上使用 @Validated。

### Q: @NotBlank 和 @NotEmpty 有什么区别？
A: @NotBlank 只用于字符串，校验 null、空字符串和纯空白字符。@NotEmpty 可用于字符串、集合、数组，校验 null 和空（size=0）。

### Q: 如何自定义错误消息？
A: 方式1：在注解中直接指定 `message = "自定义消息"`；方式2：在 `messages.properties` 中配置 `javax.validation.constraints.NotNull.message=自定义消息`。

### Q: 校验失败后代码还会继续执行吗？
A: 不会。校验失败会抛出异常，被全局异常处理器捕获后直接返回错误响应。

## 学习资源

- [Spring Validation 官方文档](https://spring.io/projects/spring-framework#learn)
- [Bean Validation 规范](https://beanvalidation.org/)
- [Hibernate Validator 文档](https://hibernate.org/validator/)

## 许可证

本项目仅用于学习交流。
