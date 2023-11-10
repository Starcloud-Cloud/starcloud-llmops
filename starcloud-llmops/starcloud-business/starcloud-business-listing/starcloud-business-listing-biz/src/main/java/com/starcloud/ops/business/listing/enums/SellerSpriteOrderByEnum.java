package com.starcloud.ops.business.listing.enums;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 卖家精灵站点枚举
 */
@Getter
@AllArgsConstructor
public enum SellerSpriteOrderByEnum implements IEnumable<Integer> {
    TRAFFIC_PERCENTAGE(12, "流量占比", null),
    RELATION_VARIATIONS_ITEMS(20, "相关 ASIN", null),
    ABA_WEEK( 4, "ABA 周排名", null),

    ABA(23, "ABA 月排名", KeywordMetadataDO::getSearchRank),
    SEARCH(5, "月搜索量", KeywordMetadataDO::getSearches),
    PURCHASES(6, "月购买量", KeywordMetadataDO::getPurchases),
    PURCHASE_RATE(7, "购买率", KeywordMetadataDO::getPurchaseRate),
    SPR(16, " SPR", KeywordMetadataDO::getSpr),
    TITLE_DENSITY(15, " 标题密度", KeywordMetadataDO::getTitleDensity),
    PRODUCTS(8, "商品数", KeywordMetadataDO::getProducts),
    SUPPLY_DEMAND_RATIO(9, "供需比", KeywordMetadataDO::getSupplyDemandRatio),
    AD_PRODUCTS(22, "广告竞品数", KeywordMetadataDO::getAdProducts),
    MONOPOLY_CLICK_RATE(18, "点击集中度", KeywordMetadataDO::getMonopolyClickRate),
    BID(11, " PPC竞价", KeywordMetadataDO::getBid),
    AVG_PRICE(17, "均价", KeywordMetadataDO::getAvgPrice),
    AVG_REVIEWS(20, "均分数", KeywordMetadataDO::getAvgReviews),
    AVG_RATING(19, "评分值", KeywordMetadataDO::getAvgRating),


    ;

    private Integer sellerSpriteCode;

    private String name;

    private SFunction<KeywordMetadataDO, ?> orderColumn;


    public static SellerSpriteOrderByEnum getByCode(Integer code) {
        return ArrayUtil.firstMatch(o -> o.getSellerSpriteCode().equals(code), values());
    }

    /**
     * 获取枚举编码
     *
     * @return 枚举值
     */
    @Override
    public Integer getCode() {
        return sellerSpriteCode;
    }

    /**
     * 获取枚举标签
     *
     * @return 枚举标签
     */
    @Override
    public String getLabel() {
        return name;
    }

    /**
     * 获取描述 <br>
     *
     * @return 描述
     */
    @Override
    public String getDescription() {
        return IEnumable.super.getDescription();
    }
}
