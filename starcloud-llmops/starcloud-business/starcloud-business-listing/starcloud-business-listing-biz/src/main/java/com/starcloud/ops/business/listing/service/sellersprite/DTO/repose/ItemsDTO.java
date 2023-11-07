package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ItemsDTO {

    @Schema(description = "站点")
    private String market;

    @Schema(description = "站点")
    private Long marketId;

    @Schema(description = "站点网址")
    private String website;
    /**
     * 关键词
     */
    @Schema(description = "关键词数据")
    private String keywords;

    @Schema(description = "关键词数据")
    private String keyword;

    /**
     * 关键词-中文
     */
    @Schema(description = "关键词-中文")
    private String keywordCn;
    /**
     * 关键词-日文
     */
    @Schema(description = "关键词-日文")
    private String keywordJp;
    /**
     * 前 10 产品信息
     */
    @Schema(description = "前 10 产品信息")
    private List<GkDatasDTO> gkDatas;
    /**
     * 流量占比
     */
    @Schema(description = "流量占比")
    private Double trafficPercentage;
    /**
     * 预估周曝光量
     */
    @Schema(description = "预估周曝光量")
    private Double calculatedWeeklySearches;

    /**
     * 相关 ASIN
     */
    @Schema(description = "相关 ASIN")
    private List<RelationVariationsItemsDTO> relationVariationsItems;

    /**
     * 月搜索趋势 环比变化 应该是前端计算
     */
    @Schema(description = "月搜索趋势 环比变化是前端计算")
    private List<SearchesTrendDTO> searchesTrend;
    @Schema(description = "月搜索趋势 环比变化是前端计算")
    private List<SearchesTrendsDTO> trends;

    /**
     * ABA 周排名
     */
    @Schema(description = "ABA 周排名")
    private Long searchesRank;
    /**
     * ABA 排名的数据时间
     */
    @Schema(description = "ABA 周排名开始时间")
    private Long searchesRankTimeFrom;

    @Schema(description = "ABA 周排名结束时间")
    private Long searchesRankTimeTo;
    /**
     * 月搜索量 注意 日平均前端计算
     */
    @Schema(description = "月搜索量 注意 日平均前端计算")
    private Long searches;
    /**
     * 月购买量
     */
    @Schema(description = "月购买量")
    private Long purchases;
    /**
     * 月购买量比列
     */
    @Schema(description = "月购买量比列")
    private Double purchaseRate;

    /**
     * SPR
     */
    @Schema(description = "SPR")
    private Long cprExact;
    @Schema(description = "SPR")
    private Long spr;
    /**
     * 标题密度
     */
    @Schema(description = "标题密度")
    private Long titleDensityExact;
    @Schema(description = "标题密度")
    private Long titleDensity;
    /**
     * 商品数
     */
    @Schema(description = "商品数")
    private Long products;

    /**
     * 供需比
     */
    @Schema(description = "供需比")
    private Double supplyDemandRatio;
    /**
     * 广告竞品数
     */
    @Schema(description = "1 天内广告竞品数")
    private Long latest1daysAds;

    @Schema(description = "7 天内广告竞品数")
    private Long latest7daysAds;

    @Schema(description = "30 天内广告竞品数")
    private Long latest30daysAds;

    @Schema(description = "广告竞品数")
    private Long adProducts;


    /**
     * 点击集中度
     */
    @Schema(description = "点击集中度")
    private Double monopolyClickRate;

    @Schema(description = "点击集中的数据")
    private List<MonopolyAsinDtosDTO> monopolyAsinDtos;
    /**
     * 具体的点击和转化数据
     */

        @Schema(description = "具体的点击和转化数据")
    private List<ClickTop3sDTO> clickTop3s;

    @Schema(description = "具体的点击和转化数据")
    private List<ClickTop3sDTO> araClickTop3;

    private Double top3ClickingRate;

    private Double top3ConversionRate;


    /**
     * PPC 竞价
     */
    @Schema(description = "PPC 竞价")
    private Double bid;
    /**
     * PPC 竞价 范围最小值
     */
    @Schema(description = "PPC 竞价 范围最小值")
    private Double bidMin;

    /**
     * PPC 竞价 范围最大值
     */
    @Schema(description = "PPC 竞价 范围最大值")
    private Double bidMax;

    /**
     * 搜索流量词
     */
        @Schema(description = "搜索流量词")
    private List<String> badges;


    private BaseDTO rankPosition;
    private BaseDTO adPosition;
    /**
     * 数据更新时间
     */
            @Schema(description = "数据更新时间")
    private Long updatedTime;


    private Object stats;


    /**
     * 预估 价格分布 判断哪个价格区间可能还有机会(价格差异化)，以及哪个价格区间竞争最为激烈
     */
    @Schema(description = "价格分布")
    private Double avgPrice;
    /**
     * 评分数分布：说明该市场打造新品的难度，如果中低评分数区间占比较大，说明新品进入壁垒不高
     */
    @Schema(description = "评分数")
    private Long avgReviews;
    /**
     * 评分值分布：说明该市场的成熟度，如果4.5以上的商品数很多，说明该市场很成熟，通过商品差异性建立竞争壁垒难度较大；如果3.5分商品很多，可能存在改进空间
     */
    @Schema(description = "评分值分布")
    private Double avgRating;


    private Object trafficKeywordTypes;
    private Object conversionKeywordTypes;

    private Object ac;

    private Object maxRankTime;
    private Object naturalRatio;
    private Object recommendRatio;
    private Object adRatio;
    /**
     * 分类
     */
    private List<DepartmentsDTO> departments;

    /**
     * 前三ASIN转化总占比
     */
    private Double cvsShareRate;
    private Integer wordCount;

    private String month;
    private String supplement;
    private Object relevancy;
    /**
     * 相关度  批量接口不返回数据
     */
    private Integer absoluteRelevancy;
    private Boolean amazonChoice;
    private Long searchRank;
    private Long searchWeeklyRank;


}