<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>操作成功 - Spring MVC Demo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .success-box {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .info-box {
            background-color: #d1ecf1;
            border: 1px solid #bee5eb;
            color: #0c5460;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        h1 {
            color: #155724;
        }
        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .data-table th, .data-table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        .data-table th {
            background-color: #f8f9fa;
        }
        .back-link {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .back-link:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="success-box">
        <h1>操作成功！</h1>
        <p>您的请求已成功处理。</p>
    </div>

    <div class="info-box">
        <h3>请求信息</h3>
        <p><strong>请求路径：</strong>${pageContext.request.requestURI}</p>
        <p><strong>请求方法：</strong>${pageContext.request.method}</p>
    </div>

    <%-- 使用JSTL和EL表达式显示模型数据 --%>
    <c:if test="${not empty user}">
        <div class="info-box">
            <h3>用户信息</h3>
            <table class="data-table">
                <tr>
                    <th>ID</th>
                    <td>${user.id}</td>
                </tr>
                <tr>
                    <th>用户名</th>
                    <td>${user.username}</td>
                </tr>
                <tr>
                    <th>邮箱</th>
                    <td>${user.email}</td>
                </tr>
                <tr>
                    <th>年龄</th>
                    <td>${user.age}</td>
                </tr>
            </table>
        </div>
    </c:if>

    <c:if test="${not empty product}">
        <div class="info-box">
            <h3>商品信息</h3>
            <table class="data-table">
                <tr>
                    <th>ID</th>
                    <td>${product.id}</td>
                </tr>
                <tr>
                    <th>名称</th>
                    <td>${product.name}</td>
                </tr>
                <tr>
                    <th>价格</th>
                    <td>${product.price}</td>
                </tr>
                <tr>
                    <th>库存</th>
                    <td>${product.stock}</td>
                </tr>
                <tr>
                    <th>分类</th>
                    <td>${product.category}</td>
                </tr>
            </table>
        </div>
    </c:if>

    <c:if test="${not empty message}">
        <div class="info-box">
            <p><strong>提示消息：</strong>${message}</p>
        </div>
    </c:if>

    <a href="${pageContext.request.contextPath}/" class="back-link">返回首页</a>
</body>
</html>
