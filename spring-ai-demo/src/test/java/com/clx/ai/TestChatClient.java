package com.clx.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestChatClient {

    // 测试ChatClient
    // chatclient统一对话接口
    @Test
    public void testChatClient(@Autowired OllamaChatModel ollamaChatModel){
        ChatClient chatClient = ChatClient.builder(ollamaChatModel).build();
        String content = chatClient.prompt().user("你好，你是哪个大模型")
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void testChatClient2(@Autowired OllamaChatModel ollamaChatModel){
        ChatClient chatClient = ChatClient.builder(ollamaChatModel).build();
        Flux<String> content = chatClient.prompt().user("你好，你是哪个大模型")
                .stream()
                .content();
        content.toIterable().forEach(System.out::println);
    }


}
