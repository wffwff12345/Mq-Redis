package com.example.components;

import com.example.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author cc
 */
@Component
@Slf4j
public class DisposableBeanImpl implements DisposableBean {
    @Autowired
    private RedisCache redisCache;
    @Override
    public void destroy() {
        Collection<String> keys = redisCache.keys("*");
        redisCache.deleteObject(keys);
        log.info("销毁：DisposableBeanImpl.destroy");
    }
}
