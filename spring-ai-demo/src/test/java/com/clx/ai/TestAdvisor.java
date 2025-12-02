package com.clx.ai;

import com.clx.ai.advisor.ReReadingAdvisor;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestAdvisor {

    // 测试advisor拦截器
    @Test
    public void testAdvisor(@Autowired OllamaChatModel ollamaChatModel){
        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
                // 主要用于调试
//                .defaultAdvisors(new SimpleLoggerAdvisor(),
                .defaultAdvisors(new SafeGuardAdvisor(List.of("clx")))
                .build();

        String content = chatClient.prompt().user("我叫clx，你能做什么")
                .call().content();

        System.out.println( content);

    }

    // 测试自定义拦截器advisor
    @Test
    public void testCustomizeAdvisor(@Autowired OllamaChatModel ollamaChatModel){
        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
                // 主要用于调试
//                .defaultAdvisors(new SimpleLoggerAdvisor(),
                .defaultAdvisors(new SimpleLoggerAdvisor(), new ReReadingAdvisor())
                .build();

        String content = chatClient.prompt().user("你能做什么")
                .call().content();

        System.out.println(content);

    }
}
