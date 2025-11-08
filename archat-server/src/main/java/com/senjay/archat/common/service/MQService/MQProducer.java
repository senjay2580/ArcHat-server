package com.senjay.archat.common.service.MQService;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;
@Service
@RequiredArgsConstructor
public class MQProducer {
// 实际消息队列生产者 只负责传递消息的
    private final RabbitTemplate rabbitTemplate;

    public void sendMsg(String exchange, String bindingKey, Object body) {
//        topic 对应交换机
//        msg 对应消息体
//        构造message 消息体
        rabbitTemplate.convertAndSend(exchange, bindingKey, body);
    }
}
