package com.starcloud.ops.business.listing.enums;

import cn.hutool.core.util.ArrayUtil;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 卖家精灵站点枚举
 */
@Getter
@AllArgsConstructor
public enum SellerSpriteOrderByEnum {
    ABA(1, "ABA 月排名"),
    SEARCH(6, "月搜索量"),
    PURCHASES(3, "月购买量"),
    PURCHASE_RATE(4, "购买率"),
    SPR(5, " SPR"),
    TITLE_DENSITY(35691, " 标题密度"),
    PRODUCTS(44551, "商品数"),
    SUPPLY_DEMAND_RATIO(7, "供需比"),
    AD_PRODUCTS(44571, "广告精品数"),
    MONOPOLY_CLICK_RATE(44571, "点击集中度"),
    BID(44571, " PPC竞价"),
    AVG_PRICE(44571, "均价"),
    AVG_REVIEWS(44571, "均分数"),
    AVG_RATING(44571, "评分值"),


    ;

    private Integer sellerSpriteCode;

    private String name;


    public static SellerSpriteOrderByEnum getByCode(Integer code) {
        return ArrayUtil.firstMatch(o -> o.getSellerSpriteCode().equals(code), values());
    }

}
