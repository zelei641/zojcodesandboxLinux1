package com.atzelei.zojcodesandbox;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class mqTest
{

    @Test
    @RabbitListener(queues = "zojjudgequestion")
    public void listenSimpleQueue(String message){
        log.error(message);
    }
}
