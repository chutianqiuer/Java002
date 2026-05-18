<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>请求信息 - Spring MVC Demo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
        }
        .info-card {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            padding: 20px;
        }
        .info-item {
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #dee2e6;
        }
        .info-item:last-child {
            border-bottom: none;
        }
        .info-label {
            font-weight: bold;
            color: #495057;
            margin-bottom: 5px;
        }
        .info-value {
            color: #212529;
            font-family: monospace;
            background-color: #e9ecef;
            padding: 5px;
            border-radius: 3px;
        }
        .btn {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="info-card">
        <h1>HTTP请求信息</h1>

        <div class="info-item">
            <div class="info-label">Context Path（应用路径）</div>
            <div class="info-value">${contextPath}</div>
        </div>

        <div class="info-item">
            <div class="info-label">Request URI（请求URI）</div>
            <div class="info-value">${requestURI}</div>
        </div>

        <div class="info-item">
            <div class="info-label">Request Method（请求方法）</div>
            <div class="info-value">${method}</div>
        </div>

        <div class="info-item">
            <div class="info-label">协议版本</div>
            <div class="info-value">${pageContext.request.protocol}</div>
        </div>

        <div class="info-item">
            <div class="info-label">服务器端口</div>
            <div class="info-value">${pageContext.request.serverPort}</div>
        </div>

        <a href="${pageContext.request.contextPath}/users/list" class="btn">返回用户列表</a>
    </div>
</body>
</html>
