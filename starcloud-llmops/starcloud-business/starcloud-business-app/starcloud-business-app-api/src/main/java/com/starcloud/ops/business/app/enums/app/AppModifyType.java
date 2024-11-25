package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AppModifyType implements IEnumable<Integer> {

    MODIFY(1, "修改"),

    EXECUTE(2, "执行"),
    ;

    private final Integer code;

    /**
     * 应用类型说明
     */
    private final String label;

    public static boolean isModify(Integer code) {
        return MODIFY.getCode().equals(code);
    }

}
