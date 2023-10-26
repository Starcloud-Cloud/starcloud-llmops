package com.starcloud.ops.business.app.util;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import org.apache.commons.lang3.StringUtils;

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
     * 拼音首字母缓存
     */
    private static final Cache<String, String> PINYIN_SIMPLE_CACHE = CacheUtil.newTimedCache(1000 * 60 * 60);

    /**
     * 获取拼音缓存
     *
     * @return 拼音缓存
     */
    public static String get(String key) {
        if (PINYIN_CACHE.containsKey(key)) {
            return PINYIN_CACHE.get(key);
        }
        String spell = PinyinUtils.pinyinNoPolyphonic(key);
        if (StringUtils.isNotBlank(spell)) {
            put(key, spell);
            return spell;
        }
        return key;
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

    /**
     * 获取拼音首字母缓存
     *
     * @return 拼音首字母缓存
     */
    public static String getSimple(String key) {
        if (PINYIN_SIMPLE_CACHE.containsKey(key)) {
            return PINYIN_SIMPLE_CACHE.get(key);
        }
        String simple = PinyinUtils.pinyinFirstSimple(key);
        if (StringUtils.isNotBlank(simple)) {
            simple = simple.toLowerCase();
            putSimple(key, simple);
            return simple;
        }
        return key;
    }

    /**
     * 设置拼音首字母缓存
     *
     * @param key   键
     * @param value 值
     */
    private static void putSimple(String key, String value) {
        PINYIN_SIMPLE_CACHE.put(key, value);
    }

    /**
     * 移除拼音首字母缓存
     *
     * @param key 键
     */
    private static void removeSimple(String key) {
        if (PINYIN_SIMPLE_CACHE.containsKey(key)) {
            PINYIN_SIMPLE_CACHE.remove(key);
        }
    }

    /**
     * 清空拼音首字母缓存
     */
    private static void clearSimple() {
        PINYIN_SIMPLE_CACHE.clear();
    }
}
