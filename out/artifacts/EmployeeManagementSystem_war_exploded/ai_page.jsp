<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>员工管理 AI 助理</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        /* 局部样式定义，使用直接颜色值和 !important 避免冲突 */
        :root {
            --primary-blue-local: #4285F4;
            --font-main-local: #1f2937;
            --font-sub-local: #70757a;
            --card-bg-local: #ffffff;
        }

        body {
            margin: 0;
            padding: 0;
            font-family: 'Roboto', 'Arial', sans-serif;
        }

        /* 1. 顶部 Header 样式 */
        .ai-header {
            text-align: center;
            padding: 80px 0 0;
            position: relative;
            z-index: 5;
        }

        .ai-header h1 {
            font-size: 2.5rem;
            font-weight: 500;
            color: var(--primary-blue-local);
            display: inline;
        }

        .ai-header .username {
            font-size: 2.5rem;
            font-weight: 400;
            color: var(--font-main-local);
            display: inline;
            margin-left: 5px;
        }

        /* 2. 修复右上角按钮显示问题 (已确认可见，但保留 !important 规则) */
        .utility-links {
            position: absolute;
            top: 10px;
            right: 10px;
            display: flex;
            gap: 8px;
            z-index: 999;
        }

        .utility-links a {
            font-size: 0.85rem !important;
            font-weight: 500 !important;
            padding: 6px 12px !important;
            border-radius: 20px !important;
            text-decoration: none !important;
            line-height: 1 !important;
            background-color: transparent !important;
            color: #1f2937 !important;
            border: 1px solid #dadce0 !important;
        }

        .btn-logout {
            color: white !important;
            background-color: #d93025 !important;
            border-color: #d93025 !important;
        }

        /* 3. 聊天输入区域容器 */
        .chat-input-container {
            display: flex;
            justify-content: center;
            align-items: center;
            position: fixed;
            bottom: 10vh;
            left: 0;
            width: 100%;
            height: auto;
            transition: bottom 0.3s ease-out;
            z-index: 100;
        }

        /* 4. 输入框卡片 */
        .input-card {
            width: 100%;
            max-width: 680px;
            background: var(--card-bg-local);
            border-radius: 28px;
            box-shadow: 0 1px 6px rgba(60, 64, 67, 0.1);
            border: 1px solid #e0e0e0;
            display: flex;
            align-items: center;
            padding: 0.6rem 1.5rem;
        }

        /* 5. 优化输入框字体和占位符 */
        #promptInput {
            flex-grow: 1;
            border: none;
            padding: 0.5rem 0;
            font-size: 1.0rem;
            color: var(--font-main-local);
            outline: none;
            background-color: transparent;
            resize: none;
            height: auto;
            max-height: 200px;
            margin-right: 10px;
        }

        /* 7. 发送按钮 */
        #sendButton {
            background-color: transparent;
            border: none;
            color: var(--primary-blue-local);
            padding: 0;
            cursor: pointer;
            transition: color 0.2s;
            font-size: 1.2rem;
            margin-left: 10px;
        }
        #sendButton i {
            font-weight: bold;
            color: var(--primary-blue-local) !important;
        }


        /* 8. 聊天历史区域 - 关键修复 */
        .chat-history-area {
            /* 首次发送消息前，保持隐藏，但通过透明度，而非 display: none */
            opacity: 0;
            visibility: hidden;
            transition: opacity 0.3s, visibility 0.3s;

            position: absolute;
            top: 150px;
            bottom: 12vh;
            left: 50%;
            transform: translateX(-50%);
            width: 90%;
            max-width: 700px;
            overflow-y: auto;
            padding: 10px;
            background-color: transparent;
            z-index: 50;
            /* 添加临时边框，检查区域是否被正确渲染 */
            /* border: 1px solid red; */
        }
        /* 聊天区域激活时的样式 */
        .chat-history-area.active {
            opacity: 1;
            visibility: visible;
        }


        /* 9. 聊天气泡样式 (使用直接颜色值和 !important 强制可见) */
        .ai-message, .user-message {
            margin-bottom: 15px;
            padding: 10px 15px;
            border-radius: 18px;
            max-width: 85%;
            line-height: 1.6;
            word-wrap: break-word;
            color: #1f2937 !important; /* 深色文字 */
        }

        .ai-message {
            background-color: #eeeeee !important; /* 浅灰色背景 */
            text-align: left;
            margin-right: auto;
            clear: both; /* 确保 AI 消息不浮动到用户消息旁边 */
        }

        .user-message {
            background-color: #e6f0ff !important; /* 浅蓝色背景 */
            margin-left: auto;
            text-align: left;
            clear: both; /* 确保用户消息独立一行 */
        }

    </style>
</head>
<body>

<div class="utility-links">
    <a href="${pageContext.request.contextPath}/DashboardServlet" class="btn-secondary">返回主页</a>
    <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">退出</a>
</div>

<div class="ai-header">
    <h1>你好!</h1>
    <span class="username"><c:out value="${sessionScope.user.username}" default="admin"/></span>
</div>

<div class="chat-history-area" id="chatHistoryArea">
</div>

<div class="chat-input-container" id="inputContainer">
    <div class="input-card">
        <form id="aiForm" style="display: flex; flex-grow: 1; align-items: center;">

            <textarea id="promptInput" name="prompt" rows="1" placeholder="问问 AI 助理" oninput="resizeTextarea()" onkeypress="handleKeypress(event)"></textarea>

            <div class="accessory-group" style="display: none;"></div>

            <button type="submit" id="sendButton" title="发送">
                <i class="fas fa-arrow-up"></i>
            </button>
        </form>
    </div>
</div>

<script>
    // 基础设置和状态
    const chatHistoryArea = document.getElementById('chatHistoryArea');
    const promptInput = document.getElementById('promptInput');
    const sendButton = document.getElementById('sendButton');
    const inputContainer = document.getElementById('inputContainer');

    window.onload = function() {
        promptInput.disabled = false;
        sendButton.disabled = false;
    };

    // 动态调整 textarea 高度
    function resizeTextarea() {
        promptInput.style.height = 'auto';
        promptInput.style.height = promptInput.scrollHeight + 'px';
    }

    // 渲染聊天气泡
    function displayMessage(text, sender) {
        // 🚨 强制打印，检查消息文本是否被接收
        console.log(`[Message Added - ${sender}]: ${text}`);

        const messageDiv = document.createElement('div');
        messageDiv.className = sender === 'user' ? 'user-message' : 'ai-message';
        messageDiv.innerText = text;

        chatHistoryArea.appendChild(messageDiv);
        chatHistoryArea.scrollTop = chatHistoryArea.scrollHeight;
    }

    // 禁用/启用输入
    function setInputEnabled(enabled) {
        promptInput.disabled = !enabled;
        sendButton.disabled = !enabled;

        const icon = sendButton.querySelector('i');
        // 启用时：蓝色箭头；禁用时：旋转加载圆圈
        icon.className = enabled ? 'fas fa-arrow-up' : 'fas fa-spinner fa-spin';
        icon.style.color = enabled ? '#4285F4' : '#70757a';

        if (enabled) {
            promptInput.focus();
        }
    }

    // 处理回车键发送
    function handleKeypress(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            document.getElementById('aiForm').dispatchEvent(new Event('submit'));
        }
    }


    document.getElementById('aiForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const prompt = promptInput.value.trim();
        if (!prompt || !sendButton.querySelector('i').classList.contains('fa-arrow-up')) return;

        // 首次聊天时：显示历史区域并将输入框移到底部
        if (!chatHistoryArea.classList.contains('active')) {
            chatHistoryArea.classList.add('active'); // 激活聊天区域
            inputContainer.style.bottom = '2vh';
        }

        // 1. 显示用户消息
        displayMessage(prompt, 'user');
        promptInput.value = '';
        resizeTextarea();

        // 2. 禁用输入并显示加载状态
        setInputEnabled(false);
        displayMessage('...', 'ai');

        const requestBody = 'prompt=' + encodeURIComponent(prompt);

        // 使用 Fetch API 调用 Servlet
        fetch('aiAssistant', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: requestBody
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.text();
            })
            .then(data => {
                // 找到加载中的消息并替换为实际回复
                const messages = chatHistoryArea.querySelectorAll('.ai-message');
                const loadingMessage = messages[messages.length - 1];

                if (loadingMessage && loadingMessage.innerText === '...') {
                    loadingMessage.innerText = data;
                } else {
                    displayMessage(data, 'ai');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                const loadingMessage = chatHistoryArea.lastElementChild;
                if (loadingMessage && loadingMessage.innerText === '...') {
                    loadingMessage.innerText = '请求失败，请检查网络或后端日志。';
                }
            })
            .finally(() => {
                // 重新启用按钮
                setInputEnabled(true);
            });
    });

</script>
</body>
</html>