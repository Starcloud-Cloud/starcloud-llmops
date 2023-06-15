package com.starcloud.ops.business.app.util.app;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.StepDTO;
import com.starcloud.ops.business.app.api.app.dto.StepWrapperDTO;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模版工具类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@SuppressWarnings("unused")
@UtilityClass
public class AppUtils {

    /**
     * 默认场景
     */
    public static final List<String> DEFAULT_SCENES = Arrays.asList("WEB_ADMIN", "WEB_MARKET");

    /**
     * 处理请求数据
     *
     * @param request 请求数据
     */
    public static void buildRequest(AppRequest request) {
        request.setName(request.getName().trim());
        request.setModel(request.getModel().toUpperCase().trim());
        request.setType(request.getType().toUpperCase().trim());
        request.setSource(request.getSource().toUpperCase().trim());
        request.setTags(StringUtil.toList(request.getTags()));
        request.setCategories(StringUtil.toList(request.getCategories()));
        request.setScenes(buildScenes(request.getScenes()));
        request.setImages(StringUtil.toList(request.getImages()));
    }

    /**
     * 将场景集合处理构建成字符串
     *
     * @param sceneList 场景列表
     * @return 场景字符串
     */
    public static List<String> buildScenes(List<String> sceneList) {
        // 如果为空，则返回默认场景
        if (CollectionUtil.isEmpty(sceneList)) {
            return DEFAULT_SCENES;
        }

        // 去重排序并且返回字符串
        return StringUtil.toList(merge(DEFAULT_SCENES, sceneList));
    }

    /**
     * 将场景字符串构建成集合
     *
     * @param scenes 场景字符串
     * @return 场景列表
     */
    public static List<String> splitScenes(String scenes) {
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
    public static String joinScenes(List<String> sceneList) {
        // 如果为空，则返回默认场景
        if (CollectionUtil.isEmpty(sceneList)) {
            return StringUtil.toString(DEFAULT_SCENES);
        }

        // 去重排序并且返回字符串
        return StringUtil.toString(merge(DEFAULT_SCENES, sceneList));
    }

    /**
     * 构建步骤图标
     *
     * @param config 模版配置
     * @return 图标字符串
     */
    public static String buildStepIcons(AppConfigDTO config) {
        return CollectionUtil.emptyIfNull(config.getSteps()).stream()
                .map(stepWrapper -> Optional.ofNullable(stepWrapper).map(StepWrapperDTO::getStep).map(StepDTO::getIcon).orElse(StringUtils.EMPTY))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

    /**
     * 构建步骤word 数量
     *
     * @param config 模版配置
     * @return word 数量
     */
    public static Integer buildWord(AppConfigDTO config) {

        return 0;
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

    /**
     * 逗号分隔符拼接字符串
     *
     * @param list 字符串集合
     * @return 拼接后的字符串
     */
    public static String join(List<String> list) {
        return StringUtil.toString(list);
    }

    /**
     * 字符串分隔符转换成集合
     *
     * @param str 字符串
     * @return 集合
     */
    public static List<String> split(String str) {
        return StringUtil.toList(str);
    }
}
