package com.senjay.archat.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.senjay.archat.common.constant.RedisConstant;
import com.senjay.archat.common.properties.JwtProperties;
import com.senjay.archat.common.user.domain.vo.request.RequestInfo;
import com.senjay.archat.common.util.JwtUtil;
import com.senjay.archat.common.util.UserHolder;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;



import java.util.concurrent.TimeUnit;

@Order(0)
@Component
public class BaseInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtProperties jwtProperties;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){

            return true;
        }

        String tokenkey = RedisConstant.LOGIN_USER_KEY + token;
        String redisToken = stringRedisTemplate.opsForValue().get(tokenkey);
        if(StrUtil.isBlank(redisToken)){

            return true;
        }
        // 刷新token
        stringRedisTemplate.expire(tokenkey,RedisConstant.LOGIN_USER_TTL, TimeUnit.MILLISECONDS);
        stringRedisTemplate.opsForValue().set(tokenkey, token, RedisConstant.LOGIN_USER_TTL, TimeUnit.MILLISECONDS);
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), redisToken);
        Object userInfoObj = claims.get("userInfo");
        // /JWT 序列化 RequestInfo 对象时转成了 JSON，再反序列化回来默认就是 Map 类型
        RequestInfo userInfo = BeanUtil.toBean(userInfoObj, RequestInfo.class);

        UserHolder.set(userInfo);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}
