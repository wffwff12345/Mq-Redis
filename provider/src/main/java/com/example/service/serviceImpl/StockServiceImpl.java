package com.example.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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

    @Override
    public boolean decrByName(String stockName) {
        LambdaQueryWrapper<Stock> query = new LambdaQueryWrapper<>();
        query.eq(Stock::getName,stockName);
        Stock stock = this.getOne(query);
        stock.setStock(stock.getStock()-1);
        boolean id = this.updateById(stock);
        return id;
    }


}
