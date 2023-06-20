package com.starcloud.ops.business.app.util.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
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
@UtilityClass
public class AppUtils {

    /**
     * 默认场景
     */
    public static final List<String> DEFAULT_SCENES = Arrays.asList("WEB_ADMIN", "WEB_MARKET");

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
     * 生成uid, 例如：uid-1
     *
     * @param uid     uid
     * @param version 版本号
     * @return uid
     */
    public static String generateUid(String uid, Integer version) {
        return uid + "-" + version;
    }

    /**
     * 获取uid, 例如：uid-1
     *
     * @param uid uid
     * @return uid
     */
    public static String getUid(String uid) {
        String[] split = uid.split("-");
        if (split.length != 2) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_FAIL, "Un Known Error");
        }
        return split[0];
    }

    /**
     * 获取版本号, 例如：uid-1
     *
     * @param uid uid
     * @return 版本号
     */
    public static Integer getVersion(String uid) {
        String[] split = uid.split("-");
        if (split.length != 2) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_FAIL, "Un Known Error");
        }
        return Integer.valueOf(split[1]);
    }

    /**
     * 获取下一个版本
     *
     * @param version 老版本
     * @return 新版本
     */
    public static Integer nextVersion(Integer version) {
        return version + 1;
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
}
