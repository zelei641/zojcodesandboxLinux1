//package com.atzelei.zojcodesandbox.mq;
//
//import com.atzelei.zojcodesandbox.model.ExecuteCodeRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.ExchangeTypes;
//import org.springframework.amqp.rabbit.annotation.Exchange;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.QueueBinding;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalTime;
//
//@Slf4j
//@Component
//public class SpringRabbitListener
//{
//
//    public void listenSimpleQueue(String message){
//        log.error("监听到消息" + message);
//    }
//
//    @RabbitListener(queues = "work.queue")
//    public void listenWorkQueue(String message){
//        log.error("消费者1......监听到消息" + message + " " + LocalTime.now());
//        try {
//            Thread.sleep(25);
//        } catch (InterruptedException e) {
//
//        }
//    }
//
//    @RabbitListener(queues = "work.queue")
//    public void listenWorkQueue2(String message){
//        log.error("消费者2......监听到消息" + message + " " + LocalTime.now());
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//
//        }
//    }
//
//
////    @RabbitListener(queues = "fanout.queue1")
////    public void listenFanoutQueue1(String message){
////        log.error("消费者1监听到fanout.queue1消息" + message + " " + LocalTime.now());
////        try {
////            Thread.sleep(200);
////        } catch (InterruptedException e) {
////
////        }
////    }
////
////    @RabbitListener(queues = "fanout.queue2")
////    public void listenFanoutQueue2(String message){
////        log.error("消费者2监听到fanout.queue2消息" + message + " " + LocalTime.now());
////        try {
////            Thread.sleep(200);
////        } catch (InterruptedException e) {
////
////        }
////    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "direct.queue1"),
//            exchange = @Exchange(name = "judgeQuestion.direct",type = ExchangeTypes.DIRECT),
//            key = {"red", "blue"}
//    ))
//    public void listenDirectQueue1(ExecuteCodeRequest message){
//        log.error("消费者1监听到direct.queue2消息" + message.getCode() + " " + LocalTime.now());
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//
//        }
//    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "direct.queue2"),
//            exchange = @Exchange(name = "judgeQuestion.direct",type = ExchangeTypes.DIRECT),
//            key = {"red", "yellow"}
//    ))
//    public void listenDirectQueue2(String message){
//        log.error("消费者2监听到direct.queue2消息" + message + " " + LocalTime.now());
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//
//        }
//    }
//
//
//    @RabbitListener(queues = "topic.queue1")
//    public void listenTopicQueue1(String message){
//        log.error("消费者1监听到topic.queue2消息" + message + " " + LocalTime.now());
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//
//        }
//    }
//
//    @RabbitListener(queues = "topic.queue2")
//    public void listenTopicQueue2(String message){
//        log.error("消费者2监听到topic.queue2消息" + message + " " + LocalTime.now());
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//
//        }
//    }
//}
