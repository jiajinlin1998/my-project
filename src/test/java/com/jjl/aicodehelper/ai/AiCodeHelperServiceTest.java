package com.jjl.aicodehelper.ai;

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
}