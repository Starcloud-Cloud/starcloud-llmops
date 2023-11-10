package com.starcloud.ops.business.listing.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-30
 */
@Getter
public enum ListingWritingStyleEnum implements IEnumable<String> {

    /**
     * 正式
     */
    FORMAL("Formal", "正式", 1),

    /**
     * 感性的
     */
    EMOTIONAL("Emotional", "感性", 2),

    /**
     * 鼓吹的
     */
    PERSUASIVE("Persuasive", "鼓吹", 3),

    /**
     * 有激情的
     */
    PASSIONATE("Passionate", "有激情", 4),

    /**
     * 有爆发力的
     */
    EXPLOSIVE("Explosive", "有爆发力", 5),

    ;

    /**
     * 语言编码
     */
    private final String code;

    /**
     * 语言标签
     */
    private final String label;

    /**
     * 排序
     */
    private final Integer sort;

    /**
     * 构造方法
     *
     * @param code  语言编码
     * @param label 语言标签
     * @param sort  排序
     */
    ListingWritingStyleEnum(String code, String label, Integer sort) {
        this.code = code;
        this.label = label;
        this.sort = sort;
    }

    /**
     * 获取Option列表
     *
     * @return Option列表
     */
    public static List<Option> options() {
        return Arrays.stream(ListingWritingStyleEnum.values())
                .sorted(Comparator.comparingInt(ListingWritingStyleEnum::getSort))
                .map(item -> Option.of(item.getLabel(), item.getCode())).collect(Collectors.toList());
    }
}
