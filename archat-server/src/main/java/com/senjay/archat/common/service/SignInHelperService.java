package com.senjay.archat.common.service;

import com.senjay.archat.common.util.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static com.senjay.archat.common.constant.RedisConstant.SIGNIN_USER_KEY;

// TODO：加经验可以用消息队列
@RequiredArgsConstructor
@Service
public class SignInHelperService {

    private final StringRedisTemplate stringRedisTemplate;


    public Boolean checkSignIn() {
        String key = buildSignInKey();
        int day = LocalDate.now().getDayOfMonth() - 1;
        return stringRedisTemplate.opsForValue().getBit(key, day);
    }

    public void signIn() {
        // bitmap offset 从0开始
        if (checkSignIn()) {
            return ;
        }
        LocalDateTime now = LocalDateTime.now();
        int day = now.getDayOfMonth()-1;
        String key = buildSignInKey();
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            // 如果是第一次签到, 要设置过期时间
            stringRedisTemplate.opsForValue().setBit(key, day, true);
            LocalDateTime nextMonthFirstDayStart =now.plusMonths(1)
                    .withDayOfMonth(2)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
            Duration expireDuration = Duration.between(now, nextMonthFirstDayStart);
            stringRedisTemplate.expire(key, expireDuration);
        } else {
            // 已存在，直接修改redis数据
            stringRedisTemplate.opsForValue().setBit(key, day, true);
        }

    }

    public Long getSignInDetailOfMonth() {
        String key = buildSignInKey();
        if(key == null) {
            return 0L;
        }
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        long SignInDetail = 0L;
        for (int i = 0; i < daysInMonth; i++) {
            Boolean bit = stringRedisTemplate.opsForValue().getBit(key, i);
            if (Boolean.TRUE.equals(bit)) {
                SignInDetail |= (1L << i);
            }
        }
        return SignInDetail;
    }


// region 内部方法
    public String buildSignInKey() {
        Long uid = UserHolder.get().getId();
        int month = LocalDate.now().getMonthValue();
        return SIGNIN_USER_KEY + month + uid;
    }
// endregion
}
