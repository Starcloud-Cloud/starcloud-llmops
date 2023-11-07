package com.starcloud.ops.business.app.service.xhs;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import net.fastposter.client.FastposterClient;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-06
 */
@Slf4j
@SuppressWarnings("all")
public class FastPosterFactory {

    /**
     * Fastposter 客户端缓存
     */
    private static final Cache<String, FastposterClient> FASTPOSTER_CLIENT_CACHE = CacheUtil.newTimedCache(1000 * 60 * 60);

    /**
     * 根据 token 创建 FastposterClient
     *
     * @param token 模板ID
     * @return FastposterClient
     */
    public static FastposterClient factory(String token) {
        log.info("尝试从缓存中获取 FastposterClient。token: {}", token);
        if (FASTPOSTER_CLIENT_CACHE.containsKey(token) && FASTPOSTER_CLIENT_CACHE.get(token) != null) {
            log.info("从缓存中获取 FastposterClient 成功， token: {}", token);
            return FASTPOSTER_CLIENT_CACHE.get(token);
        }
        log.info("缓存中无 FastposterClient，重新构建：token: {}", token);
        FastposterClient client = FastposterClient.builder().token(token).build();
        FASTPOSTER_CLIENT_CACHE.put(token, client);
        log.info("构建 FastposterClient 成功，刷新缓存并且返回客户端，token: {}", token);
        return client;
    }

}
