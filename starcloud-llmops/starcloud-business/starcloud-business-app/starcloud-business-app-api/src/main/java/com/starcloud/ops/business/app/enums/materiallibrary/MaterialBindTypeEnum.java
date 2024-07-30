package com.starcloud.ops.business.app.enums.materiallibrary;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 用户上传的素材类型枚举
 */
@Getter
@AllArgsConstructor
public enum MaterialBindTypeEnum implements IntArrayValuable {
    //
    // APP_MAY(0, "我的应用"),
    // APP_MARKET(1, "应用市场"),
    // CREATION_PLAN(2, "创作计划"),

    APP_MAY(10, "我的应用"),
    APP_MARKET(20, "应用市场"),
    CREATION_PLAN(30, "创作计划"),
    MEMBER(40, " 用户"),
    MEMBER_COPY(50, " 用户复制"),

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

    /**
     * 判断是否是【应用市场】类型
     *
     * @param code 指定code
     * @return 是否
     */
    public static boolean isAppMay(Integer code) {
        return Objects.equals(code, APP_MAY.getCode());
    }

    /**
     * 判断是否是【应用市场】类型
     *
     * @param code 指定code
     * @return 是否
     */
    public static boolean isAppMarket(Integer code) {
        return Objects.equals(code, APP_MARKET.getCode());
    }

    /**
     * 判断是否是【应用市场】类型
     *
     * @param code 指定code
     * @return 是否
     */
    public static boolean isCreationPlan(Integer code) {
        return Objects.equals(code, CREATION_PLAN.getCode());
    }

    /**
     * 判断是否是【应用市场】类型
     *
     * @param code 指定code
     * @return 是否
     */
    public static boolean isMember(Integer code) {
        return Objects.equals(code, MEMBER.getCode());
    }


}
