package com.example.controller;
import com.example.config.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * @ClassName ReceiveHandler
 * @Description TODO
 * @Version 1.0
 */
@Component
public class ReceiveHandler {
    private static final Logger log = LoggerFactory.getLogger(ReceiveHandler.class);
    //监听email队列
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void receive_email(String msg, Message message, Channel channel) throws NoSuchFieldException, IOException {
        System.out.println("QUEUE_INFORM_EMAIL msg"+msg);
        System.out.println("QUEUE_INFORM_EMAIL message"+message.getHeaders());
        log.info("消费者A收到消息：{}", msg);
        MessageHeaders headers = message.getHeaders();
        Long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            //TODO 具体业务
            //手动确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Exception：" + e.getMessage(), e);
            boolean flag = (boolean) headers.get(AmqpHeaders.REDELIVERED);
            if (flag) {
                log.error("消息已重复处理失败,拒绝再次接收...");
                channel.basicAck(tag, false);
            } else {
                log.error("消息即将再次返回队列处理...");
                channel.basicNack(tag, false, true);
            }
        }
    }
   //监听sms队列
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS})
    public void receive_sms(Object msg, Message message, Channel channel){
        System.out.println("QUEUE_INFORM_SMS msg"+msg);
    }

}