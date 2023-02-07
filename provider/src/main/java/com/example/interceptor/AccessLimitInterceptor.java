package com.example.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.example.annotation.AccessLimit;
import com.example.utils.RedisCache;
import com.example.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {
  @Autowired
  private RedisCache redisCache;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (handler instanceof HandlerMethod) {
      HandlerMethod handlerMethod = (HandlerMethod) handler;
      AccessLimit methodAnnotation = handlerMethod.getMethodAnnotation(AccessLimit.class);
      if (Objects.isNull(methodAnnotation)) {
        return true;
      }
      int seconds = methodAnnotation.seconds();
      int maxCount = methodAnnotation.maxCount();
      String key = request.getRequestURI();
      Integer count = redisCache.getCacheObject(key);
      if (Objects.isNull(count)) {
        redisCache.setCacheObject(key, 0, seconds, TimeUnit.SECONDS);
        log.info("地址{}，在{}秒内已第一次被访问次数", key, seconds);
      } else if (count < maxCount) {
        redisCache.incrBy(key);
        redisCache.expire(key, seconds, TimeUnit.SECONDS);
        log.info("地址{}，在{}秒内第{}次被访问次数", key, seconds, count + 1);
      } else {
        log.info("地址{}，在{}秒内已达到最大访问次数", key, seconds);
        ServletUtils.renderString(response, JSONObject.toJSONString("抢购失败，再接再厉！"));
        return false;
      }
    }
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {
    HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
  }
}
