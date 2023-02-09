package com.example.service.serviceImpl;

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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
@Service
@Slf4j
public class MqSecKillServiceImpl implements MqSecKillService {
    @Autowired
    RedisCache redisCache;
    @Autowired
    StockService stockService;
    @Autowired
    ProviderMqService providerMqService;
    @Autowired
    ObjectMapper objectMapper;
    @Override
    public String secKill(Integer userId, String stockName) throws JsonProcessingException {
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
