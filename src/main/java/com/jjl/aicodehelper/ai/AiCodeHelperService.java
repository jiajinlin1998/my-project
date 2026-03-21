package com.jjl.aicodehelper.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

//@AiService
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt")
    String chat(String userMessage);

    @SystemMessage(fromResource = "system-prompt")
    Reprot chatForReprot(String userMessage);

    //学习报告
    record Reprot(String name, List<String> suggenList){};
}
