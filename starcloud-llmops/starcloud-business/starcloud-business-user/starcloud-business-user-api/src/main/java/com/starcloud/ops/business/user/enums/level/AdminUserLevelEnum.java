package com.starcloud.ops.business.user.enums.level;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 优惠劵领取方式
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum AdminUserLevelEnum implements IntArrayValuable {

    FREE(0, "免费版"), //  用户免费版等级
    TRAIL(1, "体验版"), // 用户体验版等级
    BASIC(2, "基础版"), // 用户基础版等级
    PLUS(3, "高级版"), // 用户高级版等级
    PRO(4, "团队版"), // 用户团队版等级
    ;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(AdminUserLevelEnum::getValue).toArray();

    /**
     * 值
     */
    private final Integer value;
    /**
     * 名字
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }
}