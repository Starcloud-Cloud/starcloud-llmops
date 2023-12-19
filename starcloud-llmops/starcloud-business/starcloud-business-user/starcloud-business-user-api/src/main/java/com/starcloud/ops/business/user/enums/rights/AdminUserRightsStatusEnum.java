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
public enum AdminUserRightsStatusEnum implements IntArrayValuable {

    NORMAL(1, "正常"),
    EXPIRE(2, "过期"),
    CANCEL(3, "取消"),
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

    public static AdminUserRightsStatusEnum getByType(Integer type) {
        return EnumUtil.getBy(AdminUserRightsStatusEnum.class,
                e -> Objects.equals(type, e.getType()));
    }

}
