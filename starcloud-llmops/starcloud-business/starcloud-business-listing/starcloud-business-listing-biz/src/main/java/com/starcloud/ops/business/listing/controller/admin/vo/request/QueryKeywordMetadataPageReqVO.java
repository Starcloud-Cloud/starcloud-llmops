package com.starcloud.ops.business.listing.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 关键词原数据查询 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QueryKeywordMetadataPageReqVO extends PageParam {

    /**
     * 站点
     */
    private String marketName;

    /**
     * 包含的关键词
     */
    private List<String> includeKeywords;

    /**
     * 排出的关键词
     */
    private List<String> excludeKeywords;


    /**
     * ABA周排名
     * 最小值
     */
    private Integer minSearchRank;

    /**
     * ABA周排名
     * 最大值
     */
    private Integer maxSearchRank;

    /**
     * 月搜索量
     * 最小值
     */
    private Integer minSearch;

    /**
     * 月搜索量
     * 最大值
     */
    private Integer maxSearch;

    /**
     * 月购买量
     * 最小值
     */
    private Integer minPurchases;

    /**
     * 月购买量
     * 最大值
     */
    private Integer maxPurchases;

    /**
     * 购买率
     * 最小值
     */
    private Double minPurchasesRate;

    /**
     * 购买率
     * 最大值
     */
    private Double maxPurchasesRate;

    /**
     * SPR
     * 最小值
     */
    private Integer minSPR;

    /**
     * SPR
     * 最大值
     */
    private Integer maxSPR;

    /**
     * 标题密度
     * 最小值
     */
    private Integer minTitleDensity;

    /**
     * 标题密度
     * 最大值
     */
    private Integer maxTitleDensity;

    /**
     * 商品数
     * 最小值
     */
    private Integer minProducts;

    /**
     * 商品数
     * 最大值
     */
    private Integer maxProducts;

    /**
     * 供需比 = 搜索量(需求) / 商品数(供应)
     * 最小值
     */
    private Integer minSupplyDemandRatio;

    /**
     * 供需比 = 搜索量(需求) / 商品数(供应)
     * 最大值
     */
    private Integer maxSupplyDemandRatio;

    /**
     * 广告竞品数
     * 最小值
     */
    private Integer minAdProducts;

    /**
     * 广告竞品数
     * 最大值
     */
    private Integer maxAdProducts;

    /**
     * 点击集中度
     * 最小值
     */
    private Double minMonopolyClickRate;

    /**
     * 点击集中度
     * 最大值
     */
    private Double maxMonopolyClickRate;

    /**
     * PPC竞价
     * 最小值
     */
    private Integer minBid;

    /**
     * PPC竞价
     * 最大值
     */
    private Integer maxBid;


    /**
     * 单词个数
     * 最小值
     */
    private Integer minWordCount;

    /**
     * 单词个数
     * 最大值
     */
    private Integer maxWordCount;


    /**
     * 仅AC 推荐词
     */
    private Boolean amazonChoice;

    /**
     * 倒序
     */
    private Boolean desc ;


    /**
     * 倒序
     */
    private Integer orderColumn;

}
