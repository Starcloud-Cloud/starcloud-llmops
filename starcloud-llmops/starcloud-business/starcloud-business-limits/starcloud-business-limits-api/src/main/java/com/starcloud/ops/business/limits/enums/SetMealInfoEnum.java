package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import com.starcloud.ops.business.user.enums.UserLevelEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 套餐枚举
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum SetMealInfoEnum {

    AI_PLUS("000001", "产品 1",100,"这是产品1","http://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",UserLevelEnums.PLUS),
    AI_PRO("000002", "产品 2",200,"这是产品2","http://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",UserLevelEnums.PRO),
    ;

    /**
     * 产品 Code
     */
    private final String code;

    /**
     * 产品名称
     */
    private final String name;
    /**
     * 产品价格 分
     */
    private final Integer price;

    /**
     * 产品描述
     */
    private final String description;

    /**
     * 产品图片
     */
    private final String picture;

    /**
     * 产品关联的用户等级
     */
    private final UserLevelEnums userLevelEnums;

    public static SetMealInfoEnum getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }



}
