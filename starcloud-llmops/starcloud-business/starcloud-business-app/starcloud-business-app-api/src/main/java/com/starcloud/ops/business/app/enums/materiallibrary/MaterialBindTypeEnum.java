package com.starcloud.ops.business.app.enums.materiallibrary;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户上传的素材类型枚举
 */
@Getter
@AllArgsConstructor
public enum MaterialBindTypeEnum implements IntArrayValuable {

    APP_MAY(0, "我的应用"),
    APP_MARKET(1, "应用市场"),
    CREATION_PLAN(2, "创作计划"),

    ;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(MaterialBindTypeEnum::getCode).toArray();

    /**
     * 状态值
     */
    private final Integer code;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }


}