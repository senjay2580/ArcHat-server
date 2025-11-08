package com.senjay.archat.common.service;

import com.senjay.archat.common.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketHelper {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    public boolean verify(String tokenKey) {
        String token = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_USER_KEY + tokenKey);
        return token != null && !token.isEmpty();
    }

}
