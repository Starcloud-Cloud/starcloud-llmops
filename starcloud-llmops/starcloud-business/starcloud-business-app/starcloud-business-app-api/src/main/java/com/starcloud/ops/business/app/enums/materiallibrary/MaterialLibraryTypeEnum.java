package com.starcloud.ops.business.app.enums.materiallibrary;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 素材类型枚举
 */
@Getter
@AllArgsConstructor
public enum MaterialLibraryTypeEnum implements IntArrayValuable {
    SYSTEM(10, "系统素材库"),
    MEMBER(20, "用户素材库"),


    ;


    private Integer code;

    private String desc;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(MaterialLibraryTypeEnum::getCode).toArray();


    /**
     * @return int 数组
     */
    @Override
    public int[] array() {
        return ARRAYS;
    }


    /**
     * 判断是否是【系统素材库】类型
     *
     * @param status 指定code
     * @return 是否
     */
    public static boolean isSystem(Integer status) {
        return Objects.equals(status, SYSTEM.getCode());
    }

    /**
     * 判断是否是【普通素材库】类型
     *
     * @param status 指定code
     * @return 是否
     */
    public static boolean isMember(Integer status) {
        return Objects.equals(status, MEMBER.getCode());
    }
}
