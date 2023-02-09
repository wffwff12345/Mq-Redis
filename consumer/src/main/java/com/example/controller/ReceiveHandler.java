package com.example.controller;
import com.example.configs.RabbitMqConfig;
import com.example.dto.RequestDto;
import com.example.entity.Order;
import com.example.service.OrderService;
import com.example.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @ClassName ReceiveHandler @Description TODO @Version 1.0
 */
@Component
public class ReceiveHandler {
  private static final Logger log = LoggerFactory.getLogger(com.example.components.ReceiveHandler.class);
  @Autowired OrderService orderService;
  @Autowired StockService stockService;
  @Autowired
  ObjectMapper objectMapper;
  // 监听email队列
  @RabbitListener(queues = RabbitMqConfig.ORDER_QUEUE, containerFactory = "rabbitListenerContainerFactory")
  @Transactional(rollbackFor = Exception.class)
  public void receive_email(String dto, Message message, Channel channel)
          throws NoSuchFieldException, IOException {
    RequestDto requestDto = objectMapper.readValue(dto, RequestDto.class);
    System.out.println("QUEUE_INFORM_EMAIL msg" + requestDto);
    System.out.println("QUEUE_INFORM_EMAIL message" + message.getHeaders());
    log.info("消费者A收到消息：{}", requestDto);
    MessageHeaders headers = message.getHeaders();
    Long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
    try {
      // TODO 具体业务
      Order order = new Order();
      order.setOrderName(requestDto.getStockName());
      order.setOrderUser(requestDto.getUserId().toString());
      boolean decrByName = stockService.decrByName(requestDto.getStockName());
      //boolean save = orderService.save(order);
      // 手动确认消息
      if (decrByName) {
        boolean save = orderService.save(order);
        if(save){
          channel.basicAck(tag, false);
          log.info("实现具体业务");
        }

      }
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
  @RabbitListener(queues = {RabbitMqConfig.QUEUE_INFORM_EMAIL})
  public void receive_emails(String dto, Message message, Channel channel)
          throws NoSuchFieldException, IOException {
    RequestDto requestDto = objectMapper.readValue(dto, RequestDto.class);
    System.out.println("QUEUE_INFORM_EMAIL msg" + requestDto);
    System.out.println("QUEUE_INFORM_EMAIL message" + message.getHeaders());
    log.info("消费者b收到消息：{}", requestDto);
    MessageHeaders headers = message.getHeaders();
    Long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
    try {
      Order order = new Order();
      order.setOrderName(requestDto.getStockName());
      order.setOrderUser(requestDto.getUserId().toString());
      boolean decrByName = stockService.decrByName(requestDto.getStockName());
      //boolean save = orderService.save(order);
      // 手动确认消息
      if (decrByName) {
        boolean save = orderService.save(order);
        if(save){
          channel.basicAck(tag, false);
          log.info("实现具体业务");
        }

      }
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
// 监听sms队列
  @RabbitListener(queues = {RabbitMqConfig.QUEUE_INFORM_SMS})
  public void receive_sms(Object msg, Message message, Channel channel) {
    System.out.println("QUEUE_INFORM_SMS msg" + msg);
  }
}
