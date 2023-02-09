package com.example.controller;

import com.example.service.ProviderMqService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/provider")
public class MqController {
    @Autowired
    ProviderMqService  service;
    @GetMapping("/sendMessage")
    public String SendMessage(String message) throws JsonProcessingException {
        //ervice.SendMessage(message);
        return "生产者发送消息";
    }
}
