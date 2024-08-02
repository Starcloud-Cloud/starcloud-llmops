package com.starcloud.ops.business.app.enums.comment;

import cn.hutool.core.util.ArrayUtil;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-30
 */
@Getter
@AllArgsConstructor
public enum KeywordMatchTypeEnum implements IEnumable<Integer> {


    NO_MATCH(0, "收到评论立即回复"),

    ANY_ONE(1, "包含任意关键词"),

    CONTAIN_ALL(2, "同时包含所有关键词)"),

    CONTAIN_NO(3, "不包含任一关键词"),

    EXACT_MATCH(4, "精确匹配关键词"),
    ;

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    public static KeywordMatchTypeEnum getByCode(Integer code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }
}
