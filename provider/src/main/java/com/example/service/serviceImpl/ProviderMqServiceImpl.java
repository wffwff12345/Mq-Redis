package com.example.service.serviceImpl;

import com.example.config.RabbitmqConfig;
import com.example.service.ConfirmCallbackService;
import com.example.service.ProviderMqService;
import com.example.service.ReturnCallbackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.UUID;

@Service
public class ProviderMqServiceImpl implements ProviderMqService {
    @Autowired
    RedisTemplate redisTemplate;
    //使用rabbitTemplate发送消息
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    ConfirmCallbackService confirmCallbackService;
    @Autowired
    ReturnCallbackService returnCallbackService;

    @PostConstruct
    public void init() {

    }
    @Override
    public void SendMessage(String message) throws JsonProcessingException {
        /**
         * 确保消息发送失败后可以重新返回到队列中
         * 注意：yml需要配置 publisher-returns: true
         */
        rabbitTemplate.setMandatory(true);

        /**
         * 消费者确认收到消息后，手动ack回执回调处理
         */
        rabbitTemplate.setConfirmCallback(confirmCallbackService);

        /**
         * 消息投递到队列失败回调处理
         */
        rabbitTemplate.setReturnCallback(returnCallbackService);

        /**
         * 发送消息
         * 参数：
         * 1、交换机名称
         * 2、routingKey
         * 3、消息内容
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM, "inform.email",message,new CorrelationData(UUID.randomUUID().toString()));

    }

}
