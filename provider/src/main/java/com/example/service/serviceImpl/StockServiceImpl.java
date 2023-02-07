package com.example.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Stock;
import com.example.mapper.StockMapper;
import com.example.service.StockService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {
    @Override
    public List<Stock> getAll() {

        LambdaQueryWrapper<Stock> query = new LambdaQueryWrapper<>();
        List<Stock> stocks = this.list(query);
        return stocks;
    }
}
