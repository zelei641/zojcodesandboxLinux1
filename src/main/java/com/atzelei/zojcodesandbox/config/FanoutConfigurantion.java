//package com.atzelei.zojcodesandbox.config;
//
//import org.springframework.amqp.core.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FanoutConfigurantion
//{
//    @Bean
//    public FanoutExchange fanoutExchange(){
//        //return new FanoutExchange("hmall.fanout");
//        return ExchangeBuilder.fanoutExchange("judgeQuestion.fanout").build();
//    }
//
//    @Bean
//    public Queue fanoutQueue1(){
//        //return new Queue("fanout.queue1");
//        //durable 持久化到磁盘
//        //noduable 不会持久化到磁盘
//        return QueueBuilder.durable("fanout.queue1").build();
//    }
//
//    @Bean
//    public Queue fanoutQueue2(){
//        return QueueBuilder.durable("fanout.queue2").build();
//    }
//
//    @Bean
//    public Binding fanoutQueue1Banding(Queue fanoutQueue1, FanoutExchange fanoutExchange){
//        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
//    }
//
//    @Bean
//    public Binding fanoutQueue2Banding(Queue fanoutQueue2, FanoutExchange fanoutExchange){
//        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
//    }
//
//}
