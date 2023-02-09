package com.example.controller;

import com.example.annotation.AccessLimit;
import com.example.dto.RequestDto;
import com.example.entity.Stock;
import com.example.service.ProviderMqService;
import com.example.service.StockService;
import com.example.utils.RedisCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/stock")
public class TestController {

  @Autowired StockService service;
  @Autowired ObjectMapper objectMapper;
  @Autowired ProviderMqService providerMqService;
  @Autowired RedisCache redisCache;
  @Autowired
  StockService stockService;
  @PostMapping("/request")
  @AccessLimit(seconds = 1, maxCount = 800)
  public Boolean getOne(
      @RequestParam(value = "userId") Integer userId,
      @RequestParam(value = "stockName") String stockName)
      throws JsonProcessingException {
      Boolean message = false;
      RequestDto requestDto = RequestDto.builder().userId(userId).stockName(stockName).build();
      Integer stock = redisCache.getCacheObject(stockName);
      if (stock == null) {
          List<Stock> list = stockService.getAll();
          for (Stock item : list) {
              redisCache.setCacheObject(item.getName(), item.getStock());
          }
          message=false;
      } else {
          Long decrBy = redisCache.decrBy(stockName);
          if (Objects.nonNull(decrBy) && decrBy >= 0) {
              providerMqService.Send(requestDto);
              log.info("用户：" + userId + "请求商品：" + stockName);
              message = true;
          }else {
              log.info("用户：{}秒杀{}的库存量没有剩余,秒杀结束", userId,stockName);
              message = false;
          }
      }
      return message;
  }
}

