package com.senjay.archat.common.aiChat.service;

import com.senjay.archat.common.constant.RedisConstant;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String redisKey = RedisConstant.AI_CHAT_MEMORY_KEY + memoryId.toString();
        String msgJson = redisTemplate.opsForValue().get(redisKey);
        return ChatMessageDeserializer.messagesFromJson(msgJson);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        String redisKey = RedisConstant.AI_CHAT_MEMORY_KEY + memoryId.toString();
        String msgJson = ChatMessageSerializer.messagesToJson(list);
//        会话记忆保留一天
        redisTemplate.opsForValue().set(redisKey, msgJson, Duration.ofDays(1));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        String redisKey = RedisConstant.AI_CHAT_MEMORY_KEY + memoryId.toString();
        redisTemplate.delete(redisKey);
    }
}
