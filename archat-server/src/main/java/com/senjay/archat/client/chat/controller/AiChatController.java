package com.senjay.archat.client.chat.controller;

import cn.hutool.core.lang.UUID;
import com.senjay.archat.common.aiChat.service.AiChatService;
import com.senjay.archat.common.chat.domain.dto.AiPromptDTO;
import com.senjay.archat.common.user.domain.vo.response.Result;
import com.senjay.archat.common.util.RedisUtils;
import dev.langchain4j.data.message.ChatMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

import static com.senjay.archat.common.constant.RedisConstant.AI_CHAT_MEMORY_KEY;


@RestController
@RequestMapping("/client/ai")
@RequiredArgsConstructor
public class AiChatController {
    private final AiChatService aiChatService;

    @Operation(summary = "创建会话Id")
    @PostMapping("/contactId")
    public Result<String> createAiContact() {
        String uuid = UUID.randomUUID().toString();

        if(RedisUtils.set(AI_CHAT_MEMORY_KEY + uuid, new ArrayList<ChatMessage>())){
            return Result.success(uuid);
        }
        return Result.fail("创建AI会话失败！");
    }

    @Operation(summary = "AI Chat")
    @PostMapping(value = "/chat", produces = "text/html; charset=utf-8")
    public Flux<String> testAi(@RequestBody AiPromptDTO aiPromptDTO) {
        return aiChatService.chat(aiPromptDTO.getMsg(),aiPromptDTO.getMemoryId());
    }
}
