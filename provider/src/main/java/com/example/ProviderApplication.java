package com.example;

import com.example.utils.RedisCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.mapper")
@SpringBootApplication
public class ProviderApplication {
    @Autowired
    RedisCache redisCache;
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class,args);
    }

}
