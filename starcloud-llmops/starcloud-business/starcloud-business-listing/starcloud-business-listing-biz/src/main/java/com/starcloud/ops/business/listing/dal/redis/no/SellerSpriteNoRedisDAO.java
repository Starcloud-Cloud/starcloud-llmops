package com.starcloud.ops.business.listing.dal.redis.no;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.starcloud.ops.business.listing.dal.redis.RedisKeyConstants.SELLER_SPRITE_LOCK;

/**
 * 支付序号的 Redis DAO
 *
 * @author 芋道源码
 */
@Repository
public class SellerSpriteNoRedisDAO {

    @Resource
    private RedissonClient redissonClient;

    public void lock(Long id, Long timeoutMillis, Runnable runnable) {
        String lockKey = formatKey(id);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock(timeoutMillis, TimeUnit.MILLISECONDS);
            // 执行逻辑
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    private static String formatKey(Long id) {
        return String.format(SELLER_SPRITE_LOCK, id);
    }

}
