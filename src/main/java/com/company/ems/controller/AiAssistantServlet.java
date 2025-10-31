package com.company.ems.controller;

import com.company.ems.service.AIService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/aiAssistant")
public class AiAssistantServlet extends HttpServlet {
    // 实例化 AI 服务
    private final AIService aiService = new AIService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 设置编码以正确处理中文请求和响应
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        // 1. 获取用户输入的问题
        String userPrompt = request.getParameter("prompt");

        if (userPrompt != null && !userPrompt.trim().isEmpty()) {
            // 2. 调用 AI 服务获取回复
            String aiResponse = aiService.getAiResponse(userPrompt.trim());

            // 3. 将回复作为纯文本返回
            response.getWriter().write(aiResponse);
        } else {
            response.getWriter().write("请输入您的问题。");
        }
    }
}