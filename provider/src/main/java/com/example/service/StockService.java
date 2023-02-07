package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Stock;

import java.util.List;

public interface StockService extends IService<Stock> {

    List<Stock> getAll();

}
