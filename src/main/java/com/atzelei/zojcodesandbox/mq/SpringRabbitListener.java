package com.atzelei.zojcodesandbox.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringRabbitListener
{
    @RabbitListener(queues = "zojjudgequestion")
    public void listenSimpleQueue(String message){
        log.error("监听到消息" + message);
    }
}
