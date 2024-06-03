package com.starcloud.ops.business.app.util;

import org.redisson.api.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.PIXABAY_API_KEYS_LIMIT;

/**
 * 项目启动时 加载 key  每次调用
 */
@Component
public class KeyUsageLimit {
    @Resource
    private RedissonClient redissonClient;

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final int LOCK_WAIT_TIME = 10;
    private static final int LOCK_LEASE_TIME = 10;
    private static final TimeUnit LOCK_TIME_UNIT = TimeUnit.SECONDS;

    private final List<String> keyList = Arrays.asList(
            "43908057-6d3be95c754e179c69a583867"
            // "43908057-6d3be95c754e179c69a583861",
            // "43908057-6d3be95c754e179c69a583862"
    );

    private final Map<String, RRateLimiter> rateLimiterCache = new ConcurrentHashMap<>();

    public String getNextKey() {
        for (String limitKey : keyList) {
            RLock lock = null;
            try {
                lock = redissonClient.getLock(getLockKey(limitKey));
                if (!lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, LOCK_TIME_UNIT)) {
                    continue; // 跳过当前key，尝试下一个
                }

                RRateLimiter rateLimiter = getRateLimiter(limitKey);
                if (rateLimiter.tryAcquire()) {
                    return limitKey;
                }
            } catch (InterruptedException e) {
                // 处理中断异常
                Thread.currentThread().interrupt();
                return null;
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        }
        throw exception(PIXABAY_API_KEYS_LIMIT);
    }

    private RRateLimiter getRateLimiter(String limitKey) {
        return rateLimiterCache.computeIfAbsent(limitKey, k -> {
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(k);
            rateLimiter.trySetRate(RateType.OVERALL, MAX_REQUESTS_PER_MINUTE, 1, RateIntervalUnit.MINUTES);
            return rateLimiter;
        });
    }

    private static String getLockKey(String limitKey) {
        return "LOCK:" + limitKey;
    }
}





