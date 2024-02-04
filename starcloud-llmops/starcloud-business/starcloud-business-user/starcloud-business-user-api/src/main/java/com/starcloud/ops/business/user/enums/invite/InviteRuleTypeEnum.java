package com.starcloud.ops.business.user.enums.invite;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@AllArgsConstructor
@Getter
public enum InviteRuleTypeEnum implements IntArrayValuable {

    CYCLE_EFFECT(1, "循环生效", Arrays.asList(1, 2)),
    SINGLE_EFFECT(2, "单次生效",  Arrays.asList(1, 2)),
    ;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(InviteRuleTypeEnum::getType).toArray();

    /**
     * 值
     */
    private final Integer type;
    /**
     * 名字
     */
    private final String name;


    private final List<Integer> disabledTypes;

    @Override
    public int[] array() {
        return ARRAYS;
    }


    public static InviteRuleTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(InviteRuleTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }


    public static List<Integer>  getDisabledTypes(Integer type) {
        InviteRuleTypeEnum typeEnum = getByType(type);
        return typeEnum.getDisabledTypes();
    }


}