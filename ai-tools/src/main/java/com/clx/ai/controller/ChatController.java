package com.clx.ai.controller;

import com.clx.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @GetMapping("/chat")
    public String chat(String question) {
        OllamaOptions ollamaOptions = OllamaOptions.builder().temperature(0.1).build();
        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
                .defaultOptions(ollamaOptions)
                .defaultSystem("你是AI助手，所有回复均使用中文。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor() // 输出聊天日志
                )
                .build();

        return chatClient
                // 提示词
                .prompt(question)
                // 设置可用的工具给大模型，让大模型知道有哪些工具可以使用
                .tools(new DateTimeTools())
                .call().content();
    }
}
