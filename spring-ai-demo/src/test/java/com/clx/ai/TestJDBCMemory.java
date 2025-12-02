package com.clx.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class TestJDBCMemory {

    private ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired OllamaChatModel ollamaChatModel, @Autowired ChatMemory chatMemory) {
        chatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
    @TestConfiguration
    static class TestConfig {

        @Bean
        public ChatMemory chatMemory(@Autowired JdbcChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory.builder().maxMessages(2).chatMemoryRepository(chatMemoryRepository).build();
        }
    }

    @Test
    public void testJDBCAdvisor(){
        String content = chatClient.prompt().user("我叫clx，很高兴认识你")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println(content);

        content = chatClient.prompt().user("你叫什么名字")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println(content);
    }


}
