package com.jjl.aicodehelper.ai;

import com.jjl.aicodehelper.ai.guardrail.SafeInputGuardrail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import java.util.List;

//@AiService
@InputGuardrails(SafeInputGuardrail.class)
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt")
    String chat(String userMessage);

    @SystemMessage(fromResource = "system-prompt")
    Reprot chatForReprot(String userMessage);

    //学习报告
    record Reprot(String name, List<String> suggenList){};

    @SystemMessage(fromResource = "system-prompt")
    Result<String> chatWithRag(String userMessage);

    @SystemMessage(fromResource = "system-prompt")
    Flux<String> chatStream(@MemoryId int memoryId, @UserMessage String userMessage);
}
