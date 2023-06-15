package com.starcloud.ops.framework.common.api.util;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public class StringUtil {

    /**
     * 将字符串转换成集合, 去重、排序、去空格
     *
     * @param source 字符串
     * @return 集合
     */
    public static List<String> toList(String source) {
        if (StringUtils.isBlank(source)) {
            return Collections.emptyList();
        }
        return Arrays.stream(source.split(","))
                .distinct()
                .sorted()
                .filter(StringUtils::isNotBlank)
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
                .sorted()
                .filter(StringUtils::isNotBlank)
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
                .sorted()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

}
