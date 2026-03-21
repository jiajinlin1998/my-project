package com.jjl.aicodehelper;

import com.jjl.aicodehelper.ai.AiCodeHelper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class AiCodeHelperApplicationTests {

    @Resource
    private AiCodeHelper aiCodeHelper;

    @Test
    void chat() {
        aiCodeHelper.chat("你好,我是汤圆");
    }

    @Test
    void chatWithMessage() {

        Path path = Paths.get("E:\\tupian\\1.jpg");
        String uri = path.toUri().toString();

        UserMessage userMessage = UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from(uri)
        );

        aiCodeHelper.chatWithMessage(userMessage);
    }
}
