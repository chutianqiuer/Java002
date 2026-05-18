<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户详情 - Spring MVC Demo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
        }
        .detail-card {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            padding: 20px;
        }
        .detail-item {
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #dee2e6;
        }
        .detail-item:last-child {
            border-bottom: none;
        }
        .detail-label {
            font-weight: bold;
            color: #495057;
            margin-bottom: 5px;
        }
        .detail-value {
            color: #212529;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin-top: 20px;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
    </style>
</head>
<body>
    <div class="detail-card">
        <h1>用户详情</h1>

        <div class="detail-item">
            <div class="detail-label">用户ID</div>
            <div class="detail-value">${user.id}</div>
        </div>

        <div class="detail-item">
            <div class="detail-label">用户名</div>
            <div class="detail-value">${user.username}</div>
        </div>

        <div class="detail-item">
            <div class="detail-label">邮箱</div>
            <div class="detail-value">${user.email}</div>
        </div>

        <div class="detail-item">
            <div class="detail-label">年龄</div>
            <div class="detail-value">${user.age}</div>
        </div>

        <a href="${pageContext.request.contextPath}/users/list" class="btn btn-secondary">返回列表</a>
        <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary">创建新用户</a>
    </div>
</body>
</html>
