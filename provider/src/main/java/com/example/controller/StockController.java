package com.example.controller;

import com.example.annotation.AccessLimit;
import com.example.dto.RequestDto;
import com.example.entity.Stock;
import com.example.service.ProviderMqService;
import com.example.service.StockService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {

  @Autowired RedisTemplate redisTemplate;
  @Autowired StockService service;
  @Autowired ObjectMapper objectMapper;
  @Autowired ProviderMqService providerMqService;

  @PostConstruct
  public void init() throws JsonProcessingException {
    /*String stocks = (String) redisTemplate.boundValueOps("stocks").get();
    if(stocks==null){
      List<Stock> list = service.getAll();
      redisTemplate.boundValueOps("stocks").set(objectMapper.writeValueAsString(list));

    }*/
    List<Stock> list = service.getAll();
    redisTemplate.boundValueOps("stocks").set(objectMapper.writeValueAsString(list));
  }

  @PostMapping("/request")
  @AccessLimit(seconds = 1, maxCount = 20)
  public Boolean getOne(@RequestParam(value = "userId") Integer userId,@RequestParam(value = "stockId") Integer stockId) throws JsonProcessingException {
   /* String stocks = (String) redisTemplate.boundValueOps("stocks").get();
    if (stocks == null) {
      List<Stock> list = service.getAll();
      redisTemplate.boundValueOps("stocks").set(objectMapper.writeValueAsString(list));
      return false;
    } else {
      List<Stock> stockList = objectMapper.readValue(stocks, new TypeReference<List<Stock>>() {});
      for (int i = 0; i < stockList.size(); i++) {
        Stock stock = stockList.get(i);
        if (dto.getStockId().equals(stock.getId())) {
          Integer number = stock.getStock();
          if (number - 1 > 0 || number - 1 == 0) {
            stock.setStock(number-1);
            stockList.set(i,stock);
            redisTemplate.boundValueOps("stocks").set(objectMapper.writeValueAsString(stockList));
            providerMqService.SendMessage(objectMapper.writeValueAsString(dto));
            return true;
          }

        }
      }
    }*/
    return false;
  }
  @GetMapping("/all")
  public String getStocks(){
    String stocks = (String) redisTemplate.boundValueOps("stocks").get();
    return stocks;
  }
}
