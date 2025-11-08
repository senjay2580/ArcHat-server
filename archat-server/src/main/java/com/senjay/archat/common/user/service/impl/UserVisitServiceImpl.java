package com.senjay.archat.common.user.service.impl;

import com.senjay.archat.common.constant.RedisConstant;
import com.senjay.archat.common.user.service.UserVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class UserVisitServiceImpl implements UserVisitService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void addVisitor(String uid) {
        String dateKey = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = RedisConstant.UV_USER_KEY + dateKey;
        stringRedisTemplate.opsForHyperLogLog().add(key, uid);

    }

//    yyyyMMdd 形式
    @Override
    public String countVisitor(String dateKey) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            formatter.parse(dateKey);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期格式非法，应为合法的 yyyyMMdd 格式，例如 20250709 表示 2025年7月9日");
        }
        String key = RedisConstant.UV_USER_KEY + dateKey;
        Long visitorCount = stringRedisTemplate.opsForHyperLogLog().size(key);
        return visitorCount.toString();
    }
}
