package com.example.controller;

import com.example.annotation.AccessLimit;
import com.example.annotation.LimitNumber;
import com.example.dto.RequestDto;
import com.example.entity.Stock;
import com.example.service.MqSecKillService;
import com.example.service.ProviderMqService;
import com.example.service.StockService;
import com.example.utils.RedisCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/stocks")
public class SecKillController {
  @Autowired StockService stockService;
  @Autowired StockService service;
  @Autowired ObjectMapper objectMapper;
  @Autowired ProviderMqService providerMqService;
  @Autowired RedisCache redisCache;
  @Autowired
  MqSecKillService mqSecKillService;

  @PostConstruct
  public void init() throws JsonProcessingException {
    Collection<String> keys = redisCache.keys("*");
    redisCache.deleteObject(keys);
    List<Stock> list = service.getAll();
    for (Stock stock : list) {
      redisCache.setCacheObject(stock.getName(), stock.getStock());
    }
  }

  @PostMapping("/kill")
  @LimitNumber(value = 1)
  @AccessLimit(seconds = 1, maxCount = 800)
  public String secKill(
      @RequestParam(value = "userId") Integer userId,
      @RequestParam(value = "stockName") String stockName)
      throws JsonProcessingException {
    return mqSecKillService.secKill(userId, stockName);
  }

  @GetMapping("/all")
  public Map<String, Object> getStocks() {
    return redisCache.getCacheMap("users");
  }

  @PostMapping("/test")
  @LimitNumber(value = 1)
  @AccessLimit(seconds = 1, maxCount = 800)
  public String killStock( @RequestParam(value = "userId") Integer userId,
                               @RequestParam(value = "stockName") String stockName)
          throws JsonProcessingException {
    String message = "";
    RequestDto requestDto = RequestDto.builder().userId(userId).stockName(stockName).build();
    Integer stock = redisCache.getCacheObject(stockName);
    if (stock == null) {
      List<Stock> list = stockService.getAll();
      for (Stock item : list) {
        redisCache.setCacheObject(item.getName(), item.getStock());
      }
      message="商品：" +stockName+ "初始化请稍后再试!";
    } else {
      Long decrBy = redisCache.decrBy(stockName);
      if (Objects.nonNull(decrBy) && decrBy >= 0) {
        providerMqService.SendMessage(requestDto);
        log.info("用户：" + userId + "请求商品：" + stockName);
        message = "用户" + userId + "秒杀" + stockName + "成功";
      }else {
        log.info("用户：{}秒杀{}的库存量没有剩余,秒杀结束", userId,stockName);
        message = "用户："+ userId + "秒杀"+stockName+"的库存量没有剩余,秒杀结束";
      }
    }
    return message;
  }
}
