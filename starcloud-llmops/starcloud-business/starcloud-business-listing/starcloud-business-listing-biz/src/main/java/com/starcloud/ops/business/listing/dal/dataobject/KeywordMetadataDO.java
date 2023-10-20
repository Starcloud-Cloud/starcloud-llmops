package com.starcloud.ops.business.listing.dal.dataobject;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ItemsDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@TableName("llm_keyword_meta_data")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class KeywordMetadataDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 站点
     */
    private String market;
    /**
     * 站点
     */
    private Long marketId;

    /**
     * 数据网址
     */
    private String website;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 关键词-中文
     */
    private String keywordCn;
    /**
     * 关键词-日文
     */
    private String keywordJp;
    /**
     * 前 10 产品信息
     */
    private String gkDatas;
    /**
     * 流量占比
     */
    private Double trafficPercentage;
    /**
     * 预估周曝光量
     */
    private Double calculatedWeeklySearches;

    /**
     * 相关 ASIN
     */
    private String relationVariationsItems;

    /**
     * 月搜索趋势 环比变化 应该是前端计算
     */
    private String searchesTrend;

    private String trends;

    /**
     * ABA 周排名
     */
    private Long searchesRank;
    /**
     * ABA 排名的数据时间
     */
    private Long searchesRankTimeFrom;
    private Long searchesRankTimeTo;
    /**
     * 月搜索量 注意 日平均前端计算
     */
    private Long searches;
    /**
     * 月购买量
     */
    private Long purchases;
    /**
     * 月购买量比列
     */
    private Double purchaseRate;
    /**
     * SPR
     */
    private Long cprExact;
    private Long spr;
    /**
     * 标题密度
     */
    private Long titleDensityExact;
    /**
     * 标题密度
     */
    private Long titleDensity;
    /**
     * 商品数
     */
    private Long products;

    /**
     * 供需比
     */
    private Double supplyDemandRatio;
    /**
     * 广告竞品数
     */
    private Long ad_products;
    private Long latest1daysAds;
    private Long latest7daysAds;
    private Long latest30daysAds;

    /**
     * 点击集中度
     */
    private Double monopolyClickRate;
    /**
     * 具体的点击和转化数据
     */
    private String monopolyAsinDtos;

    /**
     * PPC 竞价
     */
    private Double bid;
    /**
     * PPC 竞价 范围最小值
     */
    private Double bidMin;

    /**
     * PPC 竞价 范围最大值
     */
    private Double bidMax;

    /**
     * 搜索流量词
     */
    private String badges;

    /**
     * 预估 价格分布 判断哪个价格区间可能还有机会(价格差异化)，以及哪个价格区间竞争最为激烈
     */
    private Double avgPrice;
    /**
     * 评分数分布：说明该市场打造新品的难度，如果中低评分数区间占比较大，说明新品进入壁垒不高
     */
    private Long avgReviews;
    /**
     * 评分值分布：说明该市场的成熟度，如果4.5以上的商品数很多，说明该市场很成熟，通过商品差异性建立竞争壁垒难度较大；如果3.5分商品很多，可能存在改进空间
     */
    private Double avgRating;
    /**
     * 分类
     */
    private String departments;
    /**
     * 数据时间
     */
    private String month;
    /**
     * '关键词单词数'
     */
    private Integer wordCount;
    /**
     * '可以在亚马逊搜索'
     */
    private Boolean amazonChoice;
    /**
     * '每周搜索量'
     */
    private Long searchWeeklyRank;
    /**
     * 数据更新时间
     */
    private Long dataUpdatedTime;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 状态
     */
    private Long status;

    public KeywordMetadataDO(ItemsDTO item) {
        this.market = item.getMarket();
        this.marketId = item.getMarketId();
        this.website = item.getWebsite();
        this.keywords = item.getKeywords();
        this.keywordCn = item.getKeywordCn();
        this.keywordJp = item.getKeywordJp();
        this.gkDatas = JSONUtil.toJsonStr(item.getGkDatas());
        this.trafficPercentage = item.getTrafficPercentage();
        this.calculatedWeeklySearches = item.getCalculatedWeeklySearches();
        this.relationVariationsItems = JSONUtil.toJsonStr(item.getRelationVariationsItems());
        this.searchesTrend = JSONUtil.toJsonStr(getSearchesTrend());
        this.trends = JSONUtil.toJsonStr(item.getTrends());
        this.searchesRank = item.getSearchesRank();
        this.searchesRankTimeFrom = item.getSearchesRankTimeFrom();
        this.searchesRankTimeTo = item.getSearchesRankTimeTo();
        this.searches = item.getSearches();
        this.purchases = item.getPurchases();
        this.purchaseRate = item.getPurchaseRate();
        this.cprExact =item. getCprExact();
        this.spr =item. getSpr();
        this.titleDensity = item.getTitleDensity();
        this.titleDensityExact = item.getTitleDensityExact();
        this.products = item.getProducts();
        this.supplyDemandRatio = item.getSupplyDemandRatio();
        this.ad_products = item.getAdProducts();
        this.latest1daysAds = item.getLatest1daysAds();
        this.latest7daysAds = item.getLatest7daysAds();
        this.latest30daysAds = item.getLatest30daysAds();
        this.monopolyClickRate = item.getMonopolyClickRate();
        this.monopolyAsinDtos = JSONUtil.toJsonStr(item.getMonopolyAsinDtos());
        this.bid = item.getBid();
        this.bidMin = item.getBidMin();
        this.bidMax = item.getBidMax();
        this.badges = String.join(",", item.getBadges());
        this.avgPrice = item.getAvgPrice();
        this.avgReviews = item.getAvgReviews();
        this.avgRating = item.getAvgRating();
        this.departments = JSONUtil.toJsonStr(item.getDepartments());
        this.month = item.getMonth();
        this.wordCount = item.getWordCount();
        this.amazonChoice = item.getAmazonChoice();
        this.searchWeeklyRank = item.getSearchWeeklyRank();
        this.dataUpdatedTime = item.getUpdatedTime();
        // 可以在这里设置其他字段
    }

}
