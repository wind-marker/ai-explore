package com.clx.ai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class ReReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_TEXT_ADVISOR="{text}\n" +
            "再次阅读问题:{text}";

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 获得用户输入文本
        String inputQuery = chatClientRequest.prompt().getUserMessage().getText();
        // 拦截后替换用户文本
        String reInput = PromptTemplate.builder().template(DEFAULT_TEXT_ADVISOR).build().render(Map.of("text", inputQuery));
        ChatClientRequest request = chatClientRequest.mutate()
                .prompt(Prompt.builder().content(reInput).build())
                .build();

        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return null;
    }

    @Override
    public int getOrder() {
        // 数值越小优先级越高，返回0表示该advisor具有最高执行优先级
        return 0;
    }
}
