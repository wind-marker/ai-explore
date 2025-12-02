package com.clx.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestOllama {


    @Test
    public void testOllama(@Autowired OllamaChatModel ollamaChatModel) {
//        System.out.println(ollamaChatModel.call("你好啊"));
        // 关闭思考（但是好像很多大模型已经关闭思考输出了）
        System.out.println(ollamaChatModel.call("你是谁?/no_think"));

    }

    // ollama在0.8.0之前不支持stream+ollama，0.8.0开始支持，但支持有问题
    // springai1.0有小bug
    @Test
    public void testOllama2(@Autowired OllamaChatModel ollamaChatModel) {
        Flux<String> stream = ollamaChatModel.stream("你好，请问你是谁?/no_think");
        // 阻塞输出
        stream.toIterable().forEach(System.out::println);
    }
}
