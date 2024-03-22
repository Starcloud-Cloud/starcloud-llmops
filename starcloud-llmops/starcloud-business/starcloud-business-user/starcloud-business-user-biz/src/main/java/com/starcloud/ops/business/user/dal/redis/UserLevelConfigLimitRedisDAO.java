package com.starcloud.ops.business.user.dal.redis;

import cn.hutool.core.date.LocalDateTimeUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 用户权益限制 的 RedisDAO
 *
 * @author AlanCusack
 */
@Repository
public class UserLevelConfigLimitRedisDAO {

    private final StringRedisTemplate stringRedisTemplate;

    public UserLevelConfigLimitRedisDAO(StringRedisTemplate redisTemplate) {
        this.stringRedisTemplate = redisTemplate;
    }


    public void set(String redisKey, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(1), time, unit == null ? TimeUnit.DAYS : unit);
    }

    public Integer get(String redisKey) {
        String data = stringRedisTemplate.opsForValue().get(redisKey);
        if (data != null) {
            return Integer.valueOf(data);
        }
        return 0;
    }

    public void increment(String redisKey) {

        stringRedisTemplate.opsForValue().increment(redisKey, 1);
        // 设置过期时间
        stringRedisTemplate.expire(redisKey, Duration.ofSeconds(getRemainSecondsOneDay()));
    }


    public void delete(String redisKey) {
        stringRedisTemplate.delete(redisKey);
    }


    /**
     * 获取距离当天零点的秒数
     *
     * @return 距离当天零点的秒数
     */
    public static Long getRemainSecondsOneDay() {
        return LocalDateTimeUtil.between(LocalDateTime.now(), LocalDateTimeUtil.endOfDay(LocalDateTime.now()), ChronoUnit.SECONDS);
    }

    public String buildRedisKey(String key, Long userId) {
        return String.format(key + ":%s", userId);
    }


}
