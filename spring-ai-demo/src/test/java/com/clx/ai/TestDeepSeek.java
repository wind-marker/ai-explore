package com.clx.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestDeepSeek {

    @Test
    public void testDeepSeek(@Autowired DeepSeekChatModel deepSeekChatModel) {
        // 同步阻塞方式输出
        String call = deepSeekChatModel.call("我叫陈凌霄，你叫什么");
        System.out.println(call);
    }

    @Test
    public void testDeepSeekAsync(@Autowired DeepSeekChatModel deepSeekChatModel) {
        // 流式输出
        Flux<String> stream = deepSeekChatModel.stream("我叫陈凌霄，你叫什么");
        stream.toIterable().forEach(System.out::println);
    }

    @Test
    public void testChatOptions(@Autowired DeepSeekChatModel deepSeekChatModel){
        DeepSeekChatOptions options = DeepSeekChatOptions.builder().temperature(1.9d)
                .build();
        ChatResponse res = deepSeekChatModel.call(new Prompt("ok，我已经调用到你了", options));
        // 从 ChatMessage 中提取纯文本内容
        System.out.println(res.getResult().getOutput().getText());
    }

    @Test
    public void testChatOptions2(@Autowired DeepSeekChatModel deepSeekChatModel){
        DeepSeekChatOptions options = DeepSeekChatOptions.builder().temperature(0.1d)
                .build();
        ChatResponse res = deepSeekChatModel.call(new Prompt("ok，我已经调用到你了", options));
        // 从 ChatMessage 中提取纯文本内容
        System.out.println(res.getResult().getOutput().getText());
    }

}
