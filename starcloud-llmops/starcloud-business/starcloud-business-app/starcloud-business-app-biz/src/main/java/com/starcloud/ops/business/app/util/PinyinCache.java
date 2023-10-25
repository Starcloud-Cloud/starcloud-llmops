package com.starcloud.ops.business.app.util;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-25
 */
@SuppressWarnings("unused")
public class PinyinCache {

    /**
     * 拼音缓存
     */
    private static final Cache<String, String> PINYIN_CACHE = CacheUtil.newTimedCache(1000 * 60 * 60);

    /**
     * 获取拼音缓存
     *
     * @return 拼音缓存
     */
    public static String get(String key) {
        if (PINYIN_CACHE.containsKey(key)) {
            return PINYIN_CACHE.get(key);
        }
        put(key, PinyinUtils.pinyinNoPolyphonic(key));
        return PINYIN_CACHE.get(key);
    }

    /**
     * 设置拼音缓存
     *
     * @param key   键
     * @param value 值
     */
    public static void put(String key, String value) {
        PINYIN_CACHE.put(key, value);
    }

    /**
     * 移除拼音缓存
     *
     * @param key 键
     */
    public static void remove(String key) {
        if (PINYIN_CACHE.containsKey(key)) {
            PINYIN_CACHE.remove(key);
        }
    }

    /**
     * 清空拼音缓存
     */
    public static void clear() {
        PINYIN_CACHE.clear();
    }

}
