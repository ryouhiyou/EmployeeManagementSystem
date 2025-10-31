<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>员工管理 AI 助理</title>
    <%-- 确保 CSS 路径使用绝对路径 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* 为 AI 页面定制样式 */
        .ai-container { max-width: 800px; margin: 40px auto; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        #aiResponseArea {
            min-height: 150px;
            background: #f8f8ff;
            border-radius: 5px;
            margin-top: 15px;
            padding: 15px;
            white-space: pre-wrap;
            border: 1px solid #e0e0e0;
            line-height: 1.6;
        }
        #promptInput { width: 100%; resize: vertical; padding: 10px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        #aiForm button { margin-top: 10px; }
    </style>
</head>
<body>

<%-- 使用与 Dashboard 相同的页眉结构 --%>
<header class="main-header">
    <div class="logo">员工管理系统</div>
    <nav class="main-nav">
        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list">员工管理</a>
        <a href="${pageContext.request.contextPath}/ai_page.jsp" style="font-weight: bold;">AI 助理</a>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">退出</a>
    </nav>
</header>

<div class="content-container ai-container">
    <h1>员工管理 AI 助理</h1>

    <form id="aiForm">
        <textarea id="promptInput" name="prompt" rows="5" placeholder="输入您关于员工管理的查询 (例如：如何计算年假？)">
        </textarea><br>
        <button type="submit" class="btn-primary">获取 AI 回复</button>
    </form>

    <h2>AI 回复:</h2>
    <div id="aiResponseArea">
        欢迎使用 AI 助理。请输入您的问题...
    </div>

    <script>
        document.getElementById('aiForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const prompt = document.getElementById('promptInput').value;
            const responseArea = document.getElementById('aiResponseArea');

            if (!prompt.trim()) {
                responseArea.innerHTML = '请输入有效的问题。';
                return;
            }

            // 禁用按钮并显示加载状态
            const submitButton = document.querySelector('#aiForm button');
            submitButton.disabled = true;
            responseArea.innerHTML = '正在调用 AI，请稍候... (这可能需要几秒钟)';

            // 🚀 关键修复点：使用传统的字符串拼接，避免 JSP/EL 误解析
            const requestBody = 'prompt=' + encodeURIComponent(prompt);

            // 使用 Fetch API 调用我们部署的 Servlet
            fetch('aiAssistant', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: requestBody
            })
                .then(response => response.text())
                .then(data => {
                    responseArea.innerHTML = data; // 显示AI回复
                })
                .catch(error => {
                    console.error('Error:', error);
                    responseArea.innerHTML = '请求失败，请检查网络或后端日志。';
                })
                .finally(() => {
                    // 无论成功或失败，都重新启用按钮
                    submitButton.disabled = false;
                });
        });
    </script>
</div>
</body>
</html>