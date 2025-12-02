package com.clx.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.Map;

@SpringBootTest
public class TestPrompt {

    String lifeAssistantSystemPrompt = """
    你是一个专业的智能生活助手 `LifeAssistant`。
    
    你的核心能力包括：
    1. 健康生活指导 - 提供饮食、运动、作息等方面的科学建议
    2. 生活技巧分享 - 解决日常居家、工作、学习中的实用技巧  
    3. 时间管理优化 - 帮助用户提高效率，合理安排日程
    
    回答准则：
    - 语言亲切自然，避免使用过于专业的术语
    - 建议具体可行，提供实际操作步骤
    - 考虑不同人群的需求差异
    - 必要时提供多个解决方案供选择
    
    当前服务的用户:
    姓名:{name},年龄:{age},性别:{sex}
    """;


    @Test
    public void testSystemPrompt(@Autowired OllamaChatModel ollamaChatModel){
        // 为chatclient设置提示词
        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
                .defaultSystem(lifeAssistantSystemPrompt)
                .build();
        String content = chatClient.prompt().user("你能做什么")
                .system(each->each.param("name","陈凌霄").param("age",28).param("sex","男"))
                .call().content();
        System.out.println(content);

    }

    // 伪系统提示词
    @Test
    public void testSystemPrompt2(@Autowired OllamaChatModel ollamaChatModel){
        // 为chatclient设置提示词
        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
                .build();
        String content = chatClient.prompt().user("你能做什么")
                .system(lifeAssistantSystemPrompt)
                .system(each->each.param("name","陈凌霄").param("age",28).param("sex","男"))
                .call().content();
        System.out.println(content);

    }

    // 自定义提示词模板
    @Test
    public void testPromptTemplate(@Autowired OllamaChatModel ollamaChatModel){
        PromptTemplate promptTemplate = PromptTemplate.builder().renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("收到请回复给我<test>").build();
        String prompt = promptTemplate.render(Map.of("test","未收到"));
        ChatClient chatClient = ChatClient.builder(ollamaChatModel).build();
        String content = chatClient.prompt().system(prompt).call().content();
        System.out.println(content);
    }

    @Test
    public void testPrompt(@Autowired OllamaChatModel ollamaChatModel,
                           @Value("classpath:/files/prompt.st")Resource systemResource){
        ChatClient chatClient = ChatClient.builder(ollamaChatModel).defaultSystem(systemResource)
                .build();

        String content = chatClient.prompt().user("你能做什么")
                .system(each->each.param("name","陈凌霄").param("age",28).param("sex","男"))
                .call().content();

        System.out.println(content);

    }



}
