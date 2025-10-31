package com.company.ems.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 员工管理系统的AI服务类，通过REST API 调用 火山方舟 (Volcano Ark)。
 */
public class AIService {

    // 🚀 关键修改点 1: 使用火山方舟的 API 地址
    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";

    // 🚀 关键修改点 2: 优先从环境变量 ARK_API_KEY 读取密钥
    private static final String ARK_API_KEY = System.getenv("ARK_API_KEY");

    // 假设使用的模型，可以根据需要更改
    private static final String MODEL_NAME = "doubao-seed-1-6-251015";

    private static final Logger LOGGER = Logger.getLogger(AIService.class.getName());

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    /**
     * 根据输入的问题，调用火山方舟 API 获取回复。
     * @param prompt 用户输入的问题
     * @return AI的回复文本
     */
    public String getAiResponse(String prompt) {
        if (ARK_API_KEY == null || ARK_API_KEY.isEmpty()) {
            LOGGER.log(Level.SEVERE, "火山方舟 API Key (ARK_API_KEY) 未设置！请检查系统环境变量。");
            return "系统错误：火山方舟 AI 密钥未配置。";
        }

        // 1. 构建 JSON 请求体 (使用火山方舟兼容的 JSON 结构)
        String requestBody = String.format("""
            {
              "model": "%s",
              "max_completion_tokens": 4096,
              "messages": [
                {"content": [{"text": "你是一名专业的员工管理系统助理，回答问题需简洁准确。", "type": "text"}], "role": "system"},
                {"content": [{"text": "%s", "type": "text"}], "role": "user"}
              ]
            }
            """, MODEL_NAME, prompt.replace("\"", "\\\"")); // 确保转义引号

        try {
            // 2. 构建 HTTP 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    // 使用 ARK_API_KEY 进行授权
                    .header("Authorization", "Bearer " + ARK_API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 3. 发送请求并获取响应
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. 检查 HTTP 状态码
            if (response.statusCode() != 200) {
                LOGGER.log(Level.WARNING, "火山方舟 API 调用失败，状态码: " + response.statusCode() + ", 响应体: " + response.body());
                return "AI服务调用失败，请检查密钥或网络连接。错误码: " + response.statusCode();
            }

            // 5. 简单解析 JSON 响应 (兼容 OpenAI/火山方舟的 'content' 字段提取)
            String responseBody = response.body();
            String searchString = "\"content\":\"";
            int contentStart = responseBody.indexOf(searchString) + searchString.length();

            if (contentStart != -1 && contentStart < responseBody.length()) {
                int contentEnd = responseBody.indexOf("\"", contentStart);
                if (contentEnd != -1) {
                    return responseBody.substring(contentStart, contentEnd)
                            .replace("\\n", "\n")
                            .replace("\\\"", "\"");
                }
            }

            LOGGER.log(Level.WARNING, "无法解析 API 响应体: " + responseBody);
            return "AI服务响应解析失败。";

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "调用 火山方舟 API 时发生异常", e);
            // 改进：避免返回 'null' 错误信息
            return "系统内部错误：无法连接到AI服务器。请检查网络、防火墙或代理配置。";
        }
    }
}