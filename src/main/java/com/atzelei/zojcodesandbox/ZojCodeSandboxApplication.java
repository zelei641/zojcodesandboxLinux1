package com.atzelei.zojcodesandbox;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ZojCodeSandboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZojCodeSandboxApplication.class, args);
    }

    /**
     * rabbitmq
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
