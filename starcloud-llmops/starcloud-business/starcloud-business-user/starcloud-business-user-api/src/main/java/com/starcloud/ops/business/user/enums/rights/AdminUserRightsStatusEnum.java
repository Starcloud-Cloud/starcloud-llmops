package com.starcloud.ops.business.user.enums.rights;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 系统会员权益的业务类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum AdminUserRightsStatusEnum implements IntArrayValuable {

    NORMAL(0, "正常"),
    EXPIRE(1, "过期"),
    CANCEL(2, "取消"),
    PENDING(3, "待生效"),
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
