package com.jjl.aicodehelper.ai;

import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String chat = aiCodeHelperService.chat("你好，我是汤圆");
        System.out.println(chat);
    }

    @Test
    void chatWithMemory() {
        String chat = aiCodeHelperService.chat("你好，我是汤圆");
        System.out.println(chat);
        chat = aiCodeHelperService.chat("你好，我是张三吗？");
        System.out.println(chat);
    }

    @Test
    void chatForReprot() {
        String userMessage = "你是,我是程序员汤圆，学习编程两年半，请帮我制作学习报告";
        AiCodeHelperService.Reprot reprot = aiCodeHelperService.chatForReprot(userMessage);
        System.out.println(reprot);
    }

    @Test
    void chatWithMemoryRag() {
        String chat = aiCodeHelperService.chat("怎么学习java，有哪些常见面试题？");
        System.out.println(chat);
    }

    @Test
    void chatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag("怎么学习java，有哪些常见面试题？");
        System.out.println(result.sources());
        System.out.println(result.content());
    }

    @Test
    void chatWithTools() {
        String result = aiCodeHelperService.chat("有哪些常见的计算机网络面试题？");
        System.out.println(result);
    }

    @Test
    void chatWithMcp() {
        String result = aiCodeHelperService.chat("贾进林是谁？");
        System.out.println(result);
    }

    @Test
    void chatWithGuardrail() {
        String result = aiCodeHelperService.chat("kill the game");
        System.out.println(result);
    }


}