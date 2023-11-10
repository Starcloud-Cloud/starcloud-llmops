package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐枚举
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum ProductSignEnum {

    PRODUCT_BASIC_CONFIG( "基础版", 0, "免费版", "https://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",
            UserLevelEnums.FREE, null, null, new BenefitsStrategyTypeEnums[]{}),

    PRODUCT_PLUS_CONFIG( "魔法ai-基础版-月付", 5900, "基础版-月付", "https://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",
            UserLevelEnums.BASIC, ProductTimeEnum.MONTH, BenefitsStrategyTypeEnums.PAY_BASIC_MONTH, new BenefitsStrategyTypeEnums[]{BenefitsStrategyTypeEnums.DIRECT_DISCOUNT_10}),
    PRODUCT_PRO_CONFIG( "魔法ai-高级版-月付", 19900, "高级版-月付", "https://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",
            UserLevelEnums.PLUS, ProductTimeEnum.MONTH, BenefitsStrategyTypeEnums.PAY_PLUS_MONTH, new BenefitsStrategyTypeEnums[]{BenefitsStrategyTypeEnums.DIRECT_DISCOUNT_10}),
    ;


    /**
     *
     */
    private final String name;
    /**
     * 首次支付价格 分
     */
    private final Integer firstAmount;

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

    /**
     * 产品关联的时间等级
     */
    private final ProductTimeEnum timeType;

    /**
     * 产品关联的权益等级
     */
    private final BenefitsStrategyTypeEnums benefitsStrategyTypeEnums;

    private final BenefitsStrategyTypeEnums[] limitDiscount;




    public static List<ProductSignEnum> getBySetMealTimeType(ProductTimeEnum timeType) {
        List<ProductSignEnum> productList = new ArrayList<>();

        for (ProductSignEnum setMealInfo : values()) {
            if (setMealInfo.getTimeType() == timeType) {
                productList.add(setMealInfo);
            }
        }

        return productList;
    }



}
