package com.jjl.aicodehelper.ai.model;

import com.jjl.aicodehelper.ai.listener.ChatModelListenerConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@Data
@ConfigurationProperties(prefix = "langchain4j.community.dashscope.chat-model")
public class QwenChatModelConfig {

    private String apiKey;

    private String modelName;

    @Resource
    private ChatModelListener chatModelListener;

    @Bean
    public ChatModel myQwenChatModel() {
       return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .listeners(Collections.singletonList(chatModelListener))
                .build();
    }
}
