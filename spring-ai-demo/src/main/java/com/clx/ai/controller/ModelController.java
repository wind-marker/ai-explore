package com.clx.ai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.clx.ai.options.ModelOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@RestController
public class ModelController {

    private HashMap<String, ChatModel> modelMap = new HashMap();

    public ModelController(@Autowired DashScopeChatModel dashScopeChatModel, @Autowired DeepSeekChatModel deepSeekChatModel, @Autowired OllamaChatModel ollamaChatModel){
        modelMap.put("ollama", ollamaChatModel);
        modelMap.put("deepseek", deepSeekChatModel);
        modelMap.put("dashscope", dashScopeChatModel);
    }

    @RequestMapping(value = "/chat",produces = "text/stream;charset=utf-8")
    public Flux<String> chat(String message, ModelOptions modelOptions){
        String platform = modelOptions.getPlatform();
        ChatModel chatModel = modelMap.get(platform);

        ChatClient chatClient = ChatClient.builder(chatModel).defaultOptions(
                ChatOptions.builder().temperature(modelOptions.getTemperature())
                        .model(modelOptions.getModel()).build()
                )
                .build();
        return chatClient.prompt().user(message).stream().content();
    }

}
