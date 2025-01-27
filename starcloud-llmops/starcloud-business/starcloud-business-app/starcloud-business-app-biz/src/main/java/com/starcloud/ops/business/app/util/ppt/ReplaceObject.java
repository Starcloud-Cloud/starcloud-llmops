package com.starcloud.ops.business.app.util.ppt;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class ReplaceObject implements Serializable {

    private static final long serialVersionUID = 519942884724673321L;

    /**
     * 单词字段
     */
    private String wordFieldPrefix;

    /**
     * 单词值
     */
    private Object wordValue;

    /**
     * 翻译字段
     */
    private String translationFieldPrefix;

    /**
     * 翻译值
     */
    private Object translationValue;

    /**
     * 字段索引
     */
    private Integer index;

    public String getWordField() {
        return wordFieldPrefix + index;
    }

    public String getTranslationField() {
        return translationFieldPrefix + index;
    }

    /**
     * 创建替换对象
     *
     * @param wordFieldPrefix        单词字段
     * @param wordValue              单词值
     * @param translationFieldPrefix 翻译字段
     * @param translationValue       翻译值
     * @param index                  字段索引
     * @return 替换对象
     */
    public static ReplaceObject of(String wordFieldPrefix, Object wordValue, String translationFieldPrefix, Object translationValue, Integer index) {
        ReplaceObject replaceObject = new ReplaceObject();
        replaceObject.setWordFieldPrefix(wordFieldPrefix);
        replaceObject.setWordValue(wordValue);
        replaceObject.setTranslationFieldPrefix(translationFieldPrefix);
        replaceObject.setTranslationValue(translationValue);
        replaceObject.setIndex(index);
        return replaceObject;
    }

    /**
     * 创建替换对象
     *
     * @param wordValue        单词值
     * @param translationValue 翻译值
     * @param index            字段索引
     * @return 替换对象
     */
    public static ReplaceObject of(Object wordValue, Object translationValue, Integer index) {
        return of("word", wordValue, "translation", translationValue, index);
    }

    /**
     * 将替换对象列表转换为Map
     *
     * @param replaceObjects 替换对象列表
     * @return 替换对象Map
     */
    public static Map<String, Object> toMap(List<ReplaceObject> replaceObjects) {
        Map<String, Object> replaceObjectMap = new LinkedHashMap<>();
        for (ReplaceObject replaceObject : CollectionUtil.emptyIfNull(replaceObjects)) {
            replaceObjectMap.put(replaceObject.getWordField(), replaceObject.getWordValue());
            replaceObjectMap.put(replaceObject.getTranslationField(), replaceObject.getTranslationValue());
        }
        return replaceObjectMap;
    }

    /**
     * 获取替换对象列表中最大的索引
     *
     * @param replaceObjects 替换对象列表
     * @return 最大索引
     */
    public static int getReplaceObjectMaxIndex(List<ReplaceObject> replaceObjects) {
        return CollectionUtil.emptyIfNull(replaceObjects)
                .stream()
                .map(ReplaceObject::getIndex)
                .max(Integer::compareTo)
                .orElse(0);
    }
}
