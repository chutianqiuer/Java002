<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Spring MVC Demo - 首页</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 900px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        h2 {
            color: #495057;
            margin-top: 30px;
        }
        .section {
            margin: 20px 0;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 5px;
        }
        .feature-list {
            list-style: none;
            padding: 0;
        }
        .feature-list li {
            padding: 10px 0;
            border-bottom: 1px solid #dee2e6;
        }
        .feature-list li:last-child {
            border-bottom: none;
        }
        .feature-name {
            font-weight: bold;
            color: #007bff;
        }
        .nav-links {
            margin-top: 20px;
        }
        .nav-links a {
            display: inline-block;
            padding: 10px 20px;
            margin: 5px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        .nav-links a:hover {
            background-color: #0056b3;
        }
        .nav-links a.secondary {
            background-color: #28a745;
        }
        .nav-links a.secondary:hover {
            background-color: #1e7e34;
        }
        .description {
            color: #666;
            line-height: 1.6;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Spring MVC 深入学习示例</h1>

        <p class="description">
            本项目演示了 Spring MVC 框架的核心概念和常用功能，包括：
            请求处理流程、控制器注解、参数绑定、拦截器、全局异常处理等。
        </p>

        <h2>核心功能演示</h2>
        <div class="section">
            <ul class="feature-list">
                <li>
                    <span class="feature-name">@Controller vs @RestController</span>
                    <br>传统视图控制器 vs RESTful API控制器的区别
                </li>
                <li>
                    <span class="feature-name">@RequestMapping及其衍生注解</span>
                    <br>@GetMapping、@PostMapping、@PutMapping、@DeleteMapping
                </li>
                <li>
                    <span class="feature-name">参数绑定注解</span>
                    <br>@RequestParam、@PathVariable、@RequestBody
                </li>
                <li>
                    <span class="feature-name">Model、ModelMap、ModelAndView</span>
                    <br>向视图传递数据的不同方式
                </li>
                <li>
                    <span class="feature-name">视图解析器配置</span>
                    <br>JSP视图的解析和渲染
                </li>
                <li>
                    <span class="feature-name">拦截器</span>
                    <br>preHandle、postHandle、afterCompletion 三阶段
                </li>
                <li>
                    <span class="feature-name">全局异常处理</span>
                    <br>@ExceptionHandler、@ControllerAdvice
                </li>
            </ul>
        </div>

        <h2>功能链接</h2>
        <div class="nav-links">
            <h3>用户管理（传统MVC - 返回JSP视图）</h3>
            <a href="${pageContext.request.contextPath}/users/list">用户列表</a>
            <a href="${pageContext.request.contextPath}/users/create">创建用户</a>
            <a href="${pageContext.request.contextPath}/users/search">搜索用户</a>
            <a href="${pageContext.request.contextPath}/users/info">请求信息</a>

            <h3>商品管理（RESTful API - 返回JSON）</h3>
            <a href="${pageContext.request.contextPath}/products" class="secondary">获取所有商品</a>
        </div>

        <h2>API接口（RESTful JSON）</h2>
        <div class="section">
            <table style="width:100%; border-collapse: collapse;">
                <tr style="background-color: #e9ecef;">
                    <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">方法</th>
                    <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">URL</th>
                    <th style="padding: 10px; text-align: left; border: 1px solid #ddd;">说明</th>
                </tr>
                <tr>
                    <td style="padding: 10px; border: 1px solid #ddd;">GET</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">/products</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">获取所有商品</td>
                </tr>
                <tr>
                    <td style="padding: 10px; border: 1px solid #ddd;">GET</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">/products/{id}</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">获取单个商品</td>
                </tr>
                <tr>
                    <td style="padding: 10px; border: 1px solid #ddd;">POST</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">/products</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">创建商品</td>
                </tr>
                <tr>
                    <td style="padding: 10px; border: 1px solid #ddd;">PUT</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">/products/{id}</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">更新商品</td>
                </tr>
                <tr>
                    <td style="padding: 10px; border: 1px solid #ddd;">DELETE</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">/products/{id}</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">删除商品</td>
                </tr>
            </table>
        </div>

        <p style="color: #666; margin-top: 30px; text-align: center;">
            详细文档请查看 <code>README.md</code> 文件
        </p>
    </div>
</body>
</html>
