package com.starcloud.ops.business.user.enums.rights;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 会员积分的业务类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum AdminUserRightsTypeEnum implements IntArrayValuable {

    MAGIC_BEAN(1, "魔法豆"),
    MAGIC_IMAGE(2, "图片"),
    ;

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名字
     */
    private final String name;

    @Override
    public int[] array() {
        return new int[0];
    }

    public static AdminUserRightsTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(AdminUserRightsTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }

}
