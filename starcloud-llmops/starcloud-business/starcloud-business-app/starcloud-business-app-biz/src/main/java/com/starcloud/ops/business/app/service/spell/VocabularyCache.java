package com.starcloud.ops.business.app.service.spell;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.google.common.collect.ArrayListMultimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Py4j 字典
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class VocabularyCache {

    /**
     * 词汇表路径
     */
    private static final String VOCABULARY_PATH = "classpath:vocabulary/spell.txt";

    /**
     * 词汇表缓存 key
     */
    private static final String VOCABULARY_CACHE_KEY = "VOCABULARY_SPELL_CACHE_KEY";

    /**
     * 拼音分隔符
     */
    private static final String PINYIN_SEPARATOR = "#";

    /**
     * 词汇分隔符
     */
    private static final String WORD_SEPARATOR = "/";

    /**
     * 资源加载器
     */
    private final ResourceLoader resourceLoader;

    /**
     * 词汇表缓存，默认缓存 1 天
     */
    private final Cache<String, ArrayListMultimap<String, String>> VOCABULARY_CACHE = CacheUtil.newTimedCache(1000);


    @PostConstruct
    public void init() {
        updateVocabulary();
    }

    /**
     * 获取词汇表缓存
     *
     * @return 词汇表缓存
     */
    public ArrayListMultimap<String, String> getVocabulary() {
        if (VOCABULARY_CACHE.containsKey(VOCABULARY_CACHE_KEY)) {
            return VOCABULARY_CACHE.get(VOCABULARY_CACHE_KEY);
        }
        updateVocabulary();
        return VOCABULARY_CACHE.get(VOCABULARY_CACHE_KEY);
    }

    /**
     * 设置词汇表缓存
     *
     * @param vocabulary 词汇表缓存
     */
    public void setVocabulary(ArrayListMultimap<String, String> vocabulary) {
        VOCABULARY_CACHE.put(VOCABULARY_CACHE_KEY, vocabulary);
    }

    /**
     * 更新词汇表缓存
     */
    public void updateVocabulary() {
        ArrayListMultimap<String, String> vocabulary = loadVocabulary();
        setVocabulary(vocabulary);
    }

    /**
     * 删除词汇表缓存
     */
    public void removeVocabulary() {
        VOCABULARY_CACHE.remove(VOCABULARY_CACHE_KEY);
    }

    /**
     * 构造方法
     *
     * @param resourceLoader 资源加载器
     */
    public VocabularyCache(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 加载词汇表
     *
     * @return 词汇表
     */
    private ArrayListMultimap<String, String> loadVocabulary() {
        log.info("============ 开始处理 pinyin4j 词汇表缓存 ============");

        InputStream inputStream = null;
        BufferedReader reader = null;
        ArrayListMultimap<String, String> vocabularyMap = ArrayListMultimap.create(512, 32);
        try {
            Resource resource = resourceLoader.getResource(VOCABULARY_PATH);
            // 检查资源是否存在
            if (resource.exists()) {
                // 获取资源文件的输入流
                inputStream = resource.getInputStream();
                // 使用 BufferedReader 读取文件内容
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null && StringUtils.isNotEmpty(line)) {
                    String[] lineArray = line.split(PINYIN_SEPARATOR);
                    // 词汇表单行格式不正确
                    if (lineArray.length != 2 || StringUtils.isBlank(lineArray[0]) || StringUtils.isBlank(lineArray[1])) {
                        continue;
                    }
                    String lineKey = lineArray[0];
                    String[] lineValueArray = lineArray[1].split(WORD_SEPARATOR);

                    for (String lineValueItem : lineValueArray) {
                        if (StringUtils.isNotBlank(lineValueItem)) {
                            vocabularyMap.put(lineKey, lineValueItem.trim());
                        }
                    }
                }
            } else {
                log.error("词汇表文件不存在: {}", VOCABULARY_PATH);
            }
        } catch (IOException exception) {
            log.error("读取词汇表文件失败: {}", exception.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException exception) {
                    log.error("读取词汇表关闭流(BufferedReader)失败: {}", exception.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException exception) {
                    log.error("关闭流失败: {}", exception.getMessage());
                }
            }
        }

        log.info("共缓存词汇数量: {}", vocabularyMap.keySet().size());
        log.info("============ 处理 pinyin4j 词汇表缓存结束 ============");
        return vocabularyMap;
    }

}