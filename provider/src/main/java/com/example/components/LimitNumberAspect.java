package com.example.components;

import com.example.annotation.LimitNumber;
import com.example.utils.RedisCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 限购数量切面
 */
@Slf4j
@Aspect
@AllArgsConstructor
@Component
public class LimitNumberAspect {
    @Autowired
    private RedisCache redisCache;
    @Around("@annotation(limitNumber)")
    public Object aopInterceptor(ProceedingJoinPoint pjp, LimitNumber limitNumber) throws Throwable {
        int value = limitNumber.value();

        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;

        //获取当前执行的方法
        Method targetMethod = methodSignature.getMethod();
        log.info("当前执行的方法:{}",targetMethod.getName());
        // 参数名数组
        String[] parameterNames = ((MethodSignature) signature).getParameterNames();

        //组装幂等性唯一key
        //获取参数
        Object[] objs = pjp.getArgs();
        String extApiKey = "";
        for (Object obj:objs){
            extApiKey= extApiKey.concat(obj.toString()+":");
        }
        extApiKey = extApiKey.concat("number");
        Long number = redisCache.incrBy(extApiKey);
        if(Objects.nonNull(number) && number>=value+1){
            //代理方法的返回值
            log.info("用户{}已没有抢购机会",objs[0]);
            throw new Exception("您已没有抢购机会");

        }
        return pjp.proceed();
    }
}
