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
public enum MaterialFormatTypeEnum implements IntArrayValuable {

    EXCEL(0, "表格"),
    IMAGE(1, "图片");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(MaterialFormatTypeEnum::getCode).toArray();

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
     * 判断是否是【表格】类型
     *
     * @param status 指定状态
     * @return 是否
     */
    public static boolean isExcel(Integer status) {
        return Objects.equals(status, EXCEL.getCode());
    }

    /**
     * 判断是否是【图片】类型
     *
     * @param status 指定状态
     * @return 是否
     */
    public static boolean isImage(Integer status) {
        return Objects.equals(status, IMAGE.getCode());
    }


}
