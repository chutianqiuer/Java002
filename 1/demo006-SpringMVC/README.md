# demo006-SpringMVC

Spring MVC（Web框架）深入学习示例项目

## 项目简介

本项目演示了 Spring MVC 框架的核心概念和常用功能，通过详细的代码注释帮助开发者深入理解 Spring MVC 的工作原理。

### Spring MVC 请求处理流程

```
┌─────────────────────────────────────────────────────────────────┐
│                        请求处理流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   1. 用户发送请求                                                │
│      ──────────────────► DispatcherServlet                      │
│                            │                                     │
│                            ▼                                     │
│   2. HandlerMapping查找                                         │
│      处理器（Controller）                                        │
│                            │                                     │
│                            ▼                                     │
│   3. DispatcherServlet调用                                      │
│      HandlerAdapter执行                                          │
│      Controller方法                                              │
│                            │                                     │
│                            ▼                                     │
│   4. Controller处理业务                                          │
│      返回ModelAndView                                            │
│                            │                                     │
│                            ▼                                     │
│   5. ViewResolver解析                                            │
│      视图名找到JSP                                               │
│                            │                                     │
│                            ▼                                     │
│   6. View渲染Model                                              │
│      生成HTML响应                                                │
│                            │                                     │
│                            ▼                                     │
│   7. 响应返回给用户                                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 技术栈

- **Spring Framework**: 5.3.20
- **Spring Web MVC**: 5.3.20
- **Servlet API**: 4.0.1
- **JSP**: 2.3.3
- **JSTL**: 1.2
- **Jackson**: 2.13.3（JSON处理）
- **Maven**: 3.x

## 项目结构

```
demo006-SpringMVC/
├── pom.xml                                          # Maven配置文件
├── src/main/java/com/example/
│   ├── config/
│   │   ├── AppConfig.java            # 应用配置（替代web.xml）
│   │   └── WebConfig.java            # Web MVC配置（视图解析器、拦截器等）
│   ├── controller/
│   │   ├── UserController.java       # 用户控制器（@Controller示例）
│   │   └── ProductController.java    # 商品控制器（@RestController示例）
│   ├── model/
│   │   ├── User.java                 # 用户实体类
│   │   └── Product.java              # 商品实体类
│   ├── service/
│   │   └── UserService.java          # 用户服务类
│   ├── interceptor/
│   │   └── MyInterceptor.java        # 自定义拦截器
│   └── exception/
│       └── GlobalExceptionHandler.java # 全局异常处理器
└── src/main/webapp/
    └── WEB-INF/
        └── views/
            ├── success.jsp            # 成功页面
            └── error.jsp              # 错误页面
```

## 核心功能演示

### 1. @Controller vs @RestController

| 特性 | @Controller | @RestController |
|------|-------------|----------------|
| 返回值 | 返回视图名（String） | 直接返回body数据 |
| 使用场景 | 需要渲染JSP视图 | RESTful API开发 |
| 数据格式 | ModelAndView | JSON/XML |

### 2. @RequestMapping 及其衍生注解

```java
@Controller
@RequestMapping("/users")
public class UserController {

    // GET请求
    @GetMapping("/list")
    public String list(Model model) { ... }

    // POST请求
    @PostMapping("/create")
    public String create(User user) { ... }

    // PUT请求
    @PutMapping("/{id}")
    public ResponseEntity update(...) { ... }

    // DELETE请求
    @DeleteMapping("/{id}")
    public ResponseEntity delete(...) { ... }
}
```

### 3. 参数绑定注解

| 注解 | 作用 | 示例 |
|------|------|------|
| `@RequestParam` | 绑定请求参数 | `?name=value` |
| `@PathVariable` | 绑定URL路径变量 | `/users/{id}` |
| `@RequestBody` | 绑定请求体（JSON） | POST Body |

### 4. 拦截器工作流程

```
┌──────────────────────────────────────────────┐
│                  请求处理流程                  │
├──────────────────────────────────────────────┤
│                                              │
│  preHandle() ──────► Controller ──────► postHandle()
│  （返回true继续）        执行            （视图渲染前）
│                                              │
│           afterCompletion()
│           （最终处理）
│                                              │
└──────────────────────────────────────────────┘
```

## 如何运行

### 环境要求

- JDK 1.8 或更高版本
- Maven 3.x
- Tomcat 9.x 或更高版本（支持Servlet 4.0）

### 步骤

#### 1. 编译项目

```bash
cd demo006-SpringMVC
mvn clean compile
```

#### 2. 打包War包

```bash
mvn clean package
```

打包成功后，会在 `target/` 目录下生成 `demo006-SpringMVC.war` 文件。

#### 3. 部署到Tomcat

**方式一：将War包复制到Tomcat**

```bash
# 复制War包到Tomcat的webapps目录
cp target/demo006-SpringMVC.war $CATALINA_HOME/webapps/

# 启动Tomcat
$CATALINA_HOME/bin/startup.sh   # Linux/Mac
$CATALINA_HOME/bin/startup.bat  # Windows
```

**方式二：使用IDE运行**

在 Eclipse/IntelliJ IDEA 中：
1. 导入Maven项目
2. 添加Tomcat服务器
3. 部署并运行

#### 4. 访问应用

启动Tomcat后，访问：

```
http://localhost:8080/demo006-SpringMVC/users/list
```

## API接口列表

### 用户管理（传统MVC，返回JSP视图）

| 方法 | URL | 说明 |
|------|-----|------|
| GET | `/users/list` | 显示用户列表页面 |
| GET | `/users/{id}` | 显示用户详情页面 |
| GET | `/users/create` | 显示创建用户表单 |
| POST | `/users/create` | 处理创建用户请求 |
| GET | `/users/search?username=xxx` | 搜索用户 |
| GET | `/users/json` | 获取用户列表（JSON） |
| GET | `/users/json/{id}` | 获取用户详情（JSON） |

### 商品管理（RESTful API，返回JSON）

| 方法 | URL | 说明 |
|------|-----|------|
| GET | `/products` | 获取所有商品 |
| GET | `/products/{id}` | 获取单个商品 |
| GET | `/products/category/{category}` | 按分类获取 |
| GET | `/products/search?name=xxx` | 搜索商品 |
| POST | `/products` | 创建商品 |
| PUT | `/products/{id}` | 更新商品 |
| PATCH | `/products/{id}` | 部分更新商品 |
| DELETE | `/products/{id}` | 删除商品 |

## 代码演示重点

### 1. Spring MVC工作流程

参考 `UserController.java` 中的 `userList()` 方法，详细注释说明了从请求到响应的完整流程。

### 2. 参数绑定

```java
// @RequestParam - 请求参数
@PostMapping("/create")
public String create(
    @RequestParam("username") String username,
    @RequestParam(value = "email", required = false, defaultValue = "") String email) { ... }

// @PathVariable - URL路径变量
@GetMapping("/{id}")
public String getUser(@PathVariable("id") Long id) { ... }

// @RequestBody - JSON请求体
@PostMapping
public Product create(@RequestBody Product product) { ... }
```

### 3. Model、ModelMap、ModelAndView

```java
// 使用Model
@GetMapping("/list")
public String list(Model model) {
    model.addAttribute("users", userList);
    return "userList";
}

// 使用ModelMap
@GetMapping("/search")
public String search(ModelMap modelMap) {
    modelMap.addAttribute("user", user);
    return "userDetail";
}

// 使用ModelAndView
@GetMapping("/{id}")
public ModelAndView getUser(@PathVariable Long id) {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("userDetail");
    mav.addObject("user", user);
    return mav;
}
```

### 4. 拦截器

```java
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
        // 请求前处理
        System.out.println("preHandle: " + request.getRequestURI());
        return true; // 返回false会中断请求
    }

    @Override
    public void postHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler,
                         ModelAndView modelAndView) {
        // 控制器执行后，视图渲染前
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) {
        // 请求完成后（无论成功与否）
    }
}
```

### 5. 全局异常处理

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) { ... }
}
```

## 配置文件说明

### AppConfig.java

- `@Configuration`：标识为配置类
- `@ComponentScan`：扫描并注册Spring Bean
- `@EnableWebMvc`：启用Spring MVC

### WebConfig.java

- `configureViewResolvers()`：配置JSP视图解析器
- `addResourceHandlers()`：配置静态资源处理
- `addInterceptors()`：注册自定义拦截器
- `configureMessageConverters()`：配置JSON消息转换器
- `addCorsMappings()`：配置跨域访问

## 常见问题

### 1. 404错误

确保Tomcat已正确部署应用，检查：
- War包是否成功解压
- URL路径是否正确（包含项目名）
- Controller路径是否匹配

### 2. JSON返回乱码

检查HTTP响应头的Content-Type是否为`application/json;charset=UTF-8`。

### 3. JSP无法渲染

确认：
- JSP文件位于`WEB-INF/views/`目录下
- ViewResolver的prefix和suffix配置正确
- JSTL标签库已正确引入

## 学习资源

- [Spring MVC官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)
- [Spring MVC工作流程详解](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet)
- [RESTful API设计指南](https://restfulapi.net/)

## 许可证

本项目仅供学习参考使用。
