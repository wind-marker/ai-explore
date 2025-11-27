package com.clx.ai;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;

@SpringBootTest
public class TestALi {

    @Test
    public void testQwen(@Autowired DashScopeChatModel dashScopeChatModel){
        String callResult = dashScopeChatModel.call("你叫什么名字");
        System.out.println(callResult);
    }

    // 文生图
    @Test
    public void testQwenImage(@Autowired DashScopeImageModel dashScopeImageModel){
        DashScopeImageOptions imageOptions = DashScopeImageOptions.builder().withModel("wan2.5-t2i-preview")
                .build();
        ImageResponse imageResponse = dashScopeImageModel.call(new ImagePrompt("一个中国小城", imageOptions));
        String url = imageResponse.getResult().getOutput().getUrl();
        System.out.println(url);
    }

    // 文生语音
    @Test
    public void testQwenTTS(@Autowired DashScopeAudioSpeechModel speechSynthesisModel){
        // 新版本模型貌似调用会出问题，原因未知
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .model("cosyvoice-v1")
                .build();

        SpeechSynthesisResponse speechSynthesisResponse = speechSynthesisModel
                .call(new SpeechSynthesisPrompt("你好啊，你是什么语音模型", options));

        File file = new File(System.getProperty("user.dir") + "/output.mp3");
        try(FileOutputStream fos = new FileOutputStream(file)){
            ByteBuffer audio = speechSynthesisResponse.getResult().getOutput().getAudio();
            fos.write(audio.array());
        }catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // 语音翻译audioText
    @Test
    public void testQwenAudioText(@Autowired DashScopeAudioTranscriptionModel transcriptionModel) throws MalformedURLException {
        // 目前有问题，不通
        DashScopeAudioTranscriptionOptions transcriptionOptions = DashScopeAudioTranscriptionOptions.builder().build();
        File audioFile = new File("output.mp3");
        UrlResource resource = new UrlResource(audioFile.toURI());

        AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt
                (resource, transcriptionOptions);
        AudioTranscriptionResponse call = transcriptionModel.call(audioTranscriptionPrompt);
        System.out.println(call.getResult().getOutput());

    }

    // 多模态 把图片语音视频给ai让它去理解
    @Test
    public void testQwenMultiModal(@Autowired DashScopeChatModel dashScopeChatModel){
        ClassPathResource file = new ClassPathResource("output.mp3");
        Media media = new Media(MimeTypeUtils.ALL, file);
        DashScopeChatOptions options = DashScopeChatOptions.builder().withMultiModel(true).build();
        Prompt prompt = Prompt.builder().chatOptions(options)
                .messages(UserMessage.builder().media(media).text("识别语音").build())
                .build();
        ChatResponse response = dashScopeChatModel.call(prompt);
        System.out.println(response.getResult().getOutput().getText());

    }

    // 文生视频
    @Test
    public void test2Video() throws NoApiKeyException, InputRequiredException {
        // 有的会输出为空
        VideoSynthesis videoSynthesis = new VideoSynthesis();
        VideoSynthesisParam param = VideoSynthesisParam.builder().model("wan2.5-t2v-preview")
                .prompt("优雅的小猫")
                .size("1280*720")
                .apiKey(System.getenv("BAI_LIAN_KEY"))
                .build();
        System.out.println("------------------------------");
        VideoSynthesisResult result = videoSynthesis.call(param);
        System.out.println(result.getOutput().getVideoUrl());
    }
}
