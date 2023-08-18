package com.starcloud.ops.business.log.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-16
 */
@Getter
public enum LogQueryTypeEnum implements IEnumable<Integer> {

    /**
     * 生成记录
     */
    GENERATE_RECORD(1, "生成记录", "Generate Record"),

    /**
     * 应用分析
     */
    APP_ANALYSIS(2, "应用分析", "App Analysis"),

    /**
     * 聊天分析
     */
    CHAT_ANALYSIS(3, "聊天分析", "Chat Analysis"),

    ;

    /**
     * 应用类型Code
     */
    private final Integer code;

    /**
     * 应用类型说明
     */
    private final String label;

    /**
     * 应用类型英文说明
     */
    private final String labelEn;

    public static final List<String> BASE_LOG_QUERY_TYPE = Arrays.asList(APP_ANALYSIS.name(), CHAT_ANALYSIS.name());

    /**
     * 构造函数
     *
     * @param code  枚举值
     * @param label 枚举描述
     */
    LogQueryTypeEnum(Integer code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }

    /**
     * 根据 code 获得枚举
     *
     * @return 枚举
     */
    public static List<Option> getOptions() {
        return Arrays.stream(values())
                .map(item -> Option.of(item.getCode(), item.getLabel(), item.getLabelEn()))
                .collect(Collectors.toList());
    }
}
