package com.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ProviderMqService {
    public void SendMessage(String message) throws JsonProcessingException;
}
