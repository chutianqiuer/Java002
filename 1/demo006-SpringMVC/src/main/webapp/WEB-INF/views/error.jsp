<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>错误页面 - Spring MVC Demo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .error-box {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .info-box {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            color: #383d41;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        h1 {
            color: #721c24;
        }
        .back-link {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #dc3545;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .back-link:hover {
            background-color: #c82333;
        }
        .stack-trace {
            background-color: #f5f5f5;
            border: 1px solid #ddd;
            padding: 10px;
            font-family: monospace;
            font-size: 12px;
            overflow-x: auto;
            white-space: pre-wrap;
            word-wrap: break-word;
            max-height: 300px;
            overflow-y: scroll;
        }
    </style>
</head>
<body>
    <div class="error-box">
        <h1>出错了！</h1>
        <p>抱歉，您的请求处理过程中发生了错误。</p>
    </div>

    <div class="info-box">
        <h3>错误信息</h3>
        <p><strong>异常类型：</strong>${exception}</p>
        <p><strong>错误消息：</strong>${message}</p>
        <p><strong>请求路径：</strong>${requestURI}</p>
    </div>

    <%-- 仅在开发环境显示详细错误信息 --%>
    <c:if test="${not empty exception}">
        <div class="info-box">
            <h3>异常详情（仅供调试）</h3>
            <div class="stack-trace">
                <%-- pageContext.exception 包含被捕获的异常 --%>
                <c:forEach var="trace" items="${pageContext.exception.stackTrace}">
                    ${trace}
                </c:forEach>
            </div>
        </div>
    </c:if>

    <div class="info-box">
        <h3>解决方案</h3>
        <ul>
            <li>检查您输入的参数是否正确</li>
            <li>如果是管理员，请查看服务器日志获取详细信息</li>
            <li>联系技术支持获取帮助</li>
        </ul>
    </div>

    <a href="${pageContext.request.contextPath}/" class="back-link">返回首页</a>
</body>
</html>
