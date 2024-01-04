package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 应用类型, 0：系统推荐应用，1：我的应用，2：下载应用
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
@Getter
public enum AppTypeEnum implements IEnumable<Integer> {

    /**
     * 系统应用
     */
    SYSTEM(0, "系统应用", "System App"),

    /**
     * 普通应用
     */
    COMMON(1, "普通应用", "Common App"),

    /**
     * 媒体矩阵
     */
    MEDIA_MATRIX(2, "媒体矩阵", "Media Matrix App");

    /**
     * 应用类型Code
     */
    private final Integer code;

    /**
     * 应用类型说明
     */
    private final String label;

    /**
     * 应用类型说明(英文)
     */
    private final String labelEn;

    /**
     * 构造函数
     *
     * @param code  应用类型 Code
     * @param label 应用类型说明
     */
    AppTypeEnum(Integer code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }

    /**
     * 获取应用类型 Option 列表
     *
     * @return 应用类型 Option 列表
     */
    public static List<Option> options(Boolean admin) {
        boolean isChina = Locale.CHINA.equals(LocaleContextHolder.getLocale());
        if (admin) {
            return Arrays.stream(values()).sorted(Comparator.comparingInt(AppTypeEnum::getCode)).map(item -> {
                Option option = new Option();
                option.setLabel(isChina ? item.getLabel() : item.getLabelEn());
                option.setValue(item.name());
                return option;
            }).collect(Collectors.toList());
        } else {
            return Arrays.stream(values()).filter(item -> !MEDIA_MATRIX.equals(item)).sorted(Comparator.comparingInt(AppTypeEnum::getCode)).map(item -> {
                Option option = new Option();
                option.setLabel(isChina ? item.getLabel() : item.getLabelEn());
                option.setValue(item.name());
                return option;
            }).collect(Collectors.toList());
        }
    }

}
