package com.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MqSecKillService {
    public  String secKill(Integer userId,String stockName) throws JsonProcessingException;
}
