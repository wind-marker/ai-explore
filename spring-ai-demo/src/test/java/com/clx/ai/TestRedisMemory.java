package com.clx.ai;

import com.alibaba.cloud.ai.memory.redis.BaseRedisChatMemoryRepository;
import com.alibaba.cloud.ai.memory.redis.JedisRedisChatMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class TestRedisMemory {


    private ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired OllamaChatModel ollamaChatModel, @Autowired ChatMemory chatMemory) {
        chatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
    @TestConfiguration
    static class RedisMemoryConfig {

        @Value("${spring.ai.memory.redis.host}")
        private String redisHost;

        @Value("${spring.ai.memory.redis.port}")
        private int redisPort;

        @Value("${spring.ai.memory.redis.password}")
        private String redisPassword;

        @Value("${spring.ai.memory.redis.timeout}")
        private int redisTimeout;

        @Bean
        public BaseRedisChatMemoryRepository baseRedisChatMemoryRepository() {
            return new JedisRedisChatMemoryRepository.RedisBuilder().host(redisHost)
                    .port(redisPort)
//                    .password(redisPassword)
                    .timeout(redisTimeout).build();
        }
        @Bean
        public ChatMemory chatMemory(@Autowired BaseRedisChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory.builder().maxMessages(10).chatMemoryRepository(chatMemoryRepository).build();
        }
    }

    @Test
    public void testRedisMemory(){
        String content = chatClient.prompt().user("我叫clx，很高兴认识你")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println(content);

        content = chatClient.prompt().user("你叫什么名字")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")).call().content();
        System.out.println(content);
    }
}
