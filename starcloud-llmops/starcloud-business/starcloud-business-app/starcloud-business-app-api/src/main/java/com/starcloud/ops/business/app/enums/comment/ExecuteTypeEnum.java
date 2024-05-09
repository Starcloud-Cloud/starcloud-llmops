package com.starcloud.ops.business.app.enums.comment;

import cn.hutool.core.util.ArrayUtil;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 */
@Getter
@AllArgsConstructor
public enum ExecuteTypeEnum implements IEnumable<Integer> {


    AUTO(0, "自动"),

    MANUAL(1, "手动"),
    ;

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    public static ExecuteTypeEnum getByCode(Integer code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }
}
