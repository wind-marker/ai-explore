package com.clx.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class TestMemory {

    private ChatClient chatClient;
    @BeforeEach
    public void init(@Autowired OllamaChatModel ollamaChatModel, @Autowired ChatMemory chatMemory) {
        chatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    // 对话记忆功能
    @Test
    public void testMemory() {
//        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
//                .defaultAdvisors(new SimpleLoggerAdvisor(), PromptChatMemoryAdvisor.builder(chatMemory).build())
//                .build();

        String content = chatClient.prompt().user("我叫clx，很高兴认识你").call().content();
        System.out.println(content);

        content = chatClient.prompt().user("你现在知道我叫什么名字吗？").call().content();
        System.out.println(content);
    }

    // 测试对话记忆功能超出条数情况，注意此时都并未区分用户的对话
    @Test
    public void testMemory2() {
//        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
//                .defaultAdvisors(new SimpleLoggerAdvisor(), PromptChatMemoryAdvisor.builder(chatMemory).build())
//                .build();

        String content = chatClient.prompt().user("我叫clx，很高兴认识你").call().content();
        System.out.println(content);

        content = chatClient.prompt().user("你叫什么名字").call().content();
        System.out.println(content);

        content = chatClient.prompt().user("你现在知道我叫什么名字吗？").call().content();
        System.out.println(content);
    }

    // 测试对话记忆功能，区分用户的对话
    @Test
    public void testMemory3() {
        String content = chatClient.prompt().user("我叫clx，很高兴认识你")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println(content);

        content = chatClient.prompt().user("你现在知道我叫什么名字吗？").call().content();
        System.out.println(content);

        System.out.println("---------------------------------------------------------------");
        content = chatClient.prompt().user("你现在知道我叫什么名字吗？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "2")).call().content();
        System.out.println( content);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ChatMemory chatMemory(@Autowired ChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory.builder().maxMessages(2).chatMemoryRepository(chatMemoryRepository).build();
        }
    }

}
