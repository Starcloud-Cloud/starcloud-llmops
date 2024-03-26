package com.starcloud.ops.framework.common.api.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public class StringUtil {

    /**
     * 将字符串转换成集合, 去重、去空格
     *
     * @param source 字符串
     * @return 集合
     */
    public static List<String> toList(String source) {
        if (StrUtil.isBlank(source)) {
            return Collections.emptyList();
        }
        return Arrays.stream(source.split(","))
                .distinct()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转换成集合, 去重、排序、去空格
     *
     * @param source 集合
     * @return 集合
     */
    public static List<String> toList(List<String> source) {
        return CollectionUtil.emptyIfNull(source).stream()
                .distinct()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转换成字符串, 去重、排序、去空格
     *
     * @param source 集合
     * @return 字符串
     */
    public static String toString(List<String> source) {
        return CollectionUtil.emptyIfNull(source).stream()
                .distinct()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

    public static Boolean isBlank(String source) {
        return StrUtil.isBlank(source);
    }

    public static Boolean isNotBlank(String source) {
        return StrUtil.isNotBlank(source);
    }

    public static void main(String[] args) {
        System.out.println(objectBlank(null));
    }

    public static Boolean objectBlank(Object object) {
        if (object instanceof String) {
            return isBlank((String) object);
        }
        return Objects.isNull(object);
    }

    public static Boolean objectNotBlank(Object object) {
        if (object instanceof String) {
            return isNotBlank((String) object);
        }
        return Objects.nonNull(object);
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param input 输入字符串
     * @return 是否包含中文
     */
    public static String detectLanguage(String input) {
        boolean containsChinese = false;

        for (char c : input.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                containsChinese = true;
                break;
            }
        }

        if (containsChinese) {
            return LanguageEnum.ZH_CN.getCode();
        } else {
            return LanguageEnum.EN_US.getCode();
        }
    }

}
