package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模版工具类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@SuppressWarnings("unused")
public class TemplateUtil {

    /**
     * 默认场景
     */
    public static final List<String> DEFAULT_SCENES = Arrays.asList("1", "2");

    /**
     * 将字符串构建成集合
     *
     * @param source 标签字符串
     * @return 标签列表
     */
    public static List<String> build(String source) {
        if (StringUtils.isBlank(source)) {
            return Collections.emptyList();
        }

        // 去重、排序、去空格、返回集合
        return StringUtil.toList(source);
    }

    /**
     * 将集合构建成字符串
     *
     * @param source 标签列表
     * @return 标签字符串
     */
    public static String build(List<String> source) {
        if (CollectionUtil.isEmpty(source)) {
            return StringUtils.EMPTY;
        }

        // 去重、排序、去空格、拼接成字符串返回
        return StringUtil.toString(source);
    }

    /**
     * 将场景字符串构建成集合
     *
     * @param scenes 场景字符串
     * @return 场景列表
     */
    public static List<String> buildScenes(String scenes) {
        if (StringUtils.isBlank(scenes)) {
            return StringUtil.toList(DEFAULT_SCENES);
        }

        List<String> sceneList = Arrays.stream(scenes.split(",")).filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(sceneList)) {
            return StringUtil.toList(DEFAULT_SCENES);
        }

        // 去重排序并且返回集合
        return StringUtil.toList(merge(DEFAULT_SCENES, sceneList));
    }

    /**
     * 将场景集合构建成字符串
     *
     * @param sceneList 场景列表
     * @return 场景字符串
     */
    public static String buildScenes(List<String> sceneList) {
        // 如果为空，则返回默认场景
        if (CollectionUtil.isEmpty(sceneList)) {
            return StringUtil.toString(DEFAULT_SCENES);
        }

        // 去重排序并且返回字符串
        return StringUtil.toString(merge(DEFAULT_SCENES, sceneList));
    }

    /**
     * 合并两个集合
     *
     * @param source 源集合
     * @param target 目标集合
     * @return 合并后的集合
     */
    public static List<String> merge(List<String> source, List<String> target) {

        List<String> diff = CollectionUtil.emptyIfNull(source).stream()
                .filter(item -> !CollectionUtil.emptyIfNull(target).contains(item))
                .collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(diff)) {
            target.addAll(diff);
        }

        return target;
    }

}
