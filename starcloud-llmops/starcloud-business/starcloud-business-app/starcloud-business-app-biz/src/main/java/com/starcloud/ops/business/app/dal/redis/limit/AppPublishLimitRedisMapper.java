package com.starcloud.ops.business.app.dal.redis.limit;

import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.dal.redis.RedisKeyConstants;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-30
 */
@SuppressWarnings("all")
@Repository
public class AppPublishLimitRedisMapper {

    @Resource
    private RedisTemplate<String, Map<String, AppPublishLimitDO>> redisTemplate;

    /**
     * 获取限流信息
     *
     * @return 限流信息
     */
    public AppPublishLimitDO get(String appUid) {
        String key = getKey();
        HashOperations<String, String, AppPublishLimitDO> opsForHash = redisTemplate.<String, AppPublishLimitDO>opsForHash();
        if (opsForHash.hasKey(key, appUid)) {
            return null;
        }
        AppPublishLimitDO limit = opsForHash.get(key, appUid);
        return limit;
    }

    /**
     * 设置限流信息
     */
    public void put(AppPublishLimitDO limitDO) {
        String key = getKey();
        HashOperations<String, String, AppPublishLimitDO> opsForHash = redisTemplate.<String, AppPublishLimitDO>opsForHash();
        opsForHash.put(key, limitDO.getAppUid(), limitDO);
    }

    /**
     * 设置限流信息
     *
     * @param list 限流信息
     */
    public void putAll(List<AppPublishLimitDO> list) {
        String key = getKey();
        HashOperations<String, String, AppPublishLimitDO> opsForHash = redisTemplate.<String, AppPublishLimitDO>opsForHash();
        opsForHash.putAll(key, list.stream().collect(Collectors.toMap(AppPublishLimitDO::getAppUid, Function.identity())));
    }

    /**
     * 删除限流信息
     */
    public void delete(String appUid) {
        String key = getKey();
        HashOperations<String, String, AppPublishLimitDO> opsForHash = redisTemplate.<String, AppPublishLimitDO>opsForHash();
        if (opsForHash.hasKey(key, appUid)) {
            opsForHash.delete(key, appUid);
        }
    }

    /**
     * 删除限流信息
     *
     * @param appUidList 应用 uid 列表
     */
    public void deleteByAppUidList(List<String> appUidList) {
        String key = getKey();
        HashOperations<String, String, AppPublishLimitDO> opsForHash = redisTemplate.<String, AppPublishLimitDO>opsForHash();
        opsForHash.delete(key, appUidList.toArray());
    }

    /**
     * Redis Key
     *
     * @return Redis Key
     */
    private String getKey() {
        return RedisKeyConstants.PUBLISH_LIMIT_RECORD_PREFIX;
    }

}
