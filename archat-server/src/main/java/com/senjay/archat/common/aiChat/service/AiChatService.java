package com.senjay.archat.common.aiChat.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

/**
 * @author 33813
注入(wire)模式设置为 "显式" 模式，意思是：
我自己手动指定要使用哪个模型，而不是让 Spring 自动注入默认的。
默认是 WiringMode.AUTOMATIC，如果你有多个模型（chatModel、streamingModel 等），就需要显式指定使用哪一个。
 */
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        streamingChatModel = "openAiStreamingChatModel",
        chatMemoryProvider = "chatMemoryProvider",
        contentRetriever = "contentRetriever"
)
public interface AiChatService {
    @SystemMessage(fromResource = "systemMsg.txt")
    public Flux<String> chat(@UserMessage String message, @MemoryId String memoryId );
}
