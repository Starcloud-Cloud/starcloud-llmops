package com.starcloud.ops.business.user.dal.redis;

import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * {@link OAuth2AccessTokenDO} 的 RedisDAO
 *
 * @author 芋道源码
 */
// @Repository
// @Component
// @AllArgsConstructor
public class UserLevelConfigLimitRedisDAO {

    private final StringRedisTemplate stringRedisTemplate;

    // 假设你有一个存储需要每天零点删除的键的集合
    private final Set<String> keysToExpireAtMidnight;

    public UserLevelConfigLimitRedisDAO(StringRedisTemplate redisTemplate, Set<String> keysToExpireAtMidnight) {
        this.stringRedisTemplate = redisTemplate;
        this.keysToExpireAtMidnight = keysToExpireAtMidnight;
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
       stringRedisTemplate.opsForValue().increment(redisKey,1);
    }


    public void delete(String redisKey) {
        stringRedisTemplate.delete(redisKey);
    }


    // @Scheduled(cron = "0 0 0 * * ?") // 每天零点执行
    // public void expireKeysAtMidnight() {
    //     for (String key : keysToExpireAtMidnight) {
    //         delete(key); // 删除键
    //     }
    // }



}
