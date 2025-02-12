package com.starcloud.ops.business.user.enums.rights;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 会员权益的业务类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum AdminUserRightsTypeEnum implements IntArrayValuable {

    // 累计且动态
    MAGIC_BEAN(1, "魔法豆",true,true),
    MAGIC_IMAGE(2, "图片",true,true),
    MATRIX_BEAN(3, " 魔法豆",true,true), // 原矩阵豆
    // 不累计且动态
    TEMPLATE(4, " 模板",true,false),
    ;

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名字
     */
    private final String name;

    /**
     * 是否动态
     */
    private final Boolean isDynamic;

    /**
     * 是否累计
     */
    private final Boolean isAdd;

    @Override
    public int[] array() {
        return new int[0];
    }

    public static AdminUserRightsTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(AdminUserRightsTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }

}
