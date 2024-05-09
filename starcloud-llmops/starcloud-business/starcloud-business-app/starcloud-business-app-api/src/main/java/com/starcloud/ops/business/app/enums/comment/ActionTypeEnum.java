package com.starcloud.ops.business.app.enums.comment;

import cn.hutool.core.util.ArrayUtil;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum ActionTypeEnum implements IEnumable<Integer> {

    RESPONSE(0, "回复"),

    LIKE(1, "点赞"),

    CONCERN(2, "关注)"),
    ;

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    public static ActionTypeEnum getByCode(Integer code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }
}
