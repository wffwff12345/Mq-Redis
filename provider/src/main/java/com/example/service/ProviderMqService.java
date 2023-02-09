package com.example.service;

import com.example.dto.RequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProviderMqService {
    public void SendMessage(RequestDto dto) throws JsonProcessingException;
    public void Send(RequestDto dto) throws JsonProcessingException;
}
