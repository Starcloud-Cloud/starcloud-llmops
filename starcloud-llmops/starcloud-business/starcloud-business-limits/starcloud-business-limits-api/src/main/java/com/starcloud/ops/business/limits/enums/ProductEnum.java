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
public enum ProductEnum {

    AI_PLUS_MONTH("plus_month", "魔法 AI Plus",9900,"魔法 AI Plus", "https://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",
            UserLevelEnums.PLUS, ProductTimeEnum.MONTH,BenefitsStrategyTypeEnums.PAY_PLUS_MONTH),
    AI_PRO_MONTH("pro_month", "魔法 AI Pro",49900,"魔法 AI Plus", "https://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",
            UserLevelEnums.PLUS, ProductTimeEnum.MONTH,BenefitsStrategyTypeEnums.PAY_PRO_MONTH),
    AI_PLUS_YEAR("plus_year", "魔法 AI Plus",99900,"魔法 AI Pro", "https://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",
            UserLevelEnums.PRO, ProductTimeEnum.YEAR,BenefitsStrategyTypeEnums.PAY_PLUS_YEAR),
    AI_PRO_YEAR("pro_year", "魔法 AI Pro",499900,"魔法 AI Pro", "https://cn-test.llmops-ui-user.hotsalestar.com/static/media/user-round.13b5a31bebd2cc6016d6db2cac8e92d1.svg",
            UserLevelEnums.PRO, ProductTimeEnum.YEAR,BenefitsStrategyTypeEnums.PAY_PRO_YEAR),
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

    /**
     * 产品关联的时间等级
     */
    private final ProductTimeEnum timeType;

    /**
     * 产品关联的权益等级
     */
    private final BenefitsStrategyTypeEnums  benefitsStrategyTypeEnums;



    public static ProductEnum getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }


    public static List<ProductEnum> getBySetMealTimeType(ProductTimeEnum timeType) {
        List<ProductEnum> productList = new ArrayList<>();

        for (ProductEnum setMealInfo : values()) {
            if (setMealInfo.getTimeType() == timeType) {
                productList.add(setMealInfo);
            }
        }

        return productList;
    }

    public static String getRoleCodeByCode(String code) {
        ProductEnum product = getByCode(code);
        if (product != null) {
            return product.getUserLevelEnums().getRoleCode();
        }
        return UserLevelEnums.FREE.getRoleCode();
    }

    public static String getBenefitsTypeByCode(String code) {
        ProductEnum product = getByCode(code);
        if (product != null) {
            return product.getBenefitsStrategyTypeEnums().getName();
        }
        throw new IllegalArgumentException("Invalid product code: " + code);
    }

}
