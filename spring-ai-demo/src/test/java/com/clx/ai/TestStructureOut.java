package com.clx.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class TestStructureOut {

    private ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired OllamaChatModel ollamaChatModel, @Autowired ChatMemory chatMemory) {
        chatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @Test
    public void testBoolOut(){
        String content = chatClient.prompt()
                .system("请判断用户是否有投诉意图？只能用true或false回答，不要输出多余内容")
                .user("你们家的货太烂了,我要退钱")
                .call()
                .content();
        System.out.println(content);

        if (Boolean.TRUE.equals(Boolean.valueOf(content))) {
            System.out.println("有投诉意图");
        } else {
            System.out.println("没有投诉意图");
        }

    }

    // Java14引进的新语法
    public record Address(
        String name,

        String privince,
        String city,
        String detail,
        String phoneNumber
    ){}

    @Test
    public void testStructureOut(){
        Address address = chatClient.prompt().system("""
                        请从下面文本中提取信息,
                        """)
                .user("姓名:clx,电话:1512345678,地址:湖北省武汉市某某街道某某号")
                .call().entity(Address.class);

        System.out.println(address);
    }

    record ActorFilms(String actor, List<String> films){

    }
    @Test
    public void testStructureOut2(@Autowired OllamaChatModel ollamaChatModel){
        BeanOutputConverter<ActorFilms> actorFilmsBeanOutputConverter = new BeanOutputConverter<>(ActorFilms.class);
        String format = actorFilmsBeanOutputConverter.getFormat();
        System.out.println(format);

        String actor = "周杰伦";

        String template = """
                提供五部{actor}的电影,
                {format}
                """;

//        ActorFilms entity = chatClient.prompt()
//                .user(template)
//                .user(each -> each.param("actor", actor).param("format", format))
//                .call().entity(ActorFilms.class);
//        System.out.println(entity);
        PromptTemplate promptTemplate = PromptTemplate.builder().template(template)
                .variables(Map.of("actor", actor, "format", format)).build();
        ChatResponse chatResponse = ollamaChatModel.call(promptTemplate.create());
        ActorFilms entity = actorFilmsBeanOutputConverter.convert(chatResponse.getResult().getOutput().getText());
        System.out.println(entity);

    }

}
