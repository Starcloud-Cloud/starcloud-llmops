package com.starcloud.ops.business.app.dal.redis.limit;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.dal.redis.RedisKeyConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-30
 */
@SuppressWarnings("unused")
@Repository
public class AppPublishLimitRedisMapper {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取限流信息
     *
     * @return 推荐的模板
     */
    public AppPublishLimitDO get(String appUid) {
        String key = getKey(appUid);
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            return null;
        }
        String limit = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(limit)) {
            return JSONUtil.toBean(limit, AppPublishLimitDO.class);
        }
        return null;
    }

    /**
     * 设置推荐的模板
     */
    public void set(AppPublishLimitDO limitDO) {
        String key = getKey(limitDO.getAppUid());
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.delete(key);
        }
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(limitDO), 600L, TimeUnit.SECONDS);
    }

    /**
     * 删除推荐的模板
     */
    public void delete(String appUid) {
        String key = getKey(appUid);
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.delete(key);
        }

    }

    /**
     * Redis Key
     *
     * @return Redis Key
     */
    private String getKey(String appUid) {
        return RedisKeyConstants.PUBLISH_LIMIT_RECORD_PREFIX + appUid;
    }

}
