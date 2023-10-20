package com.starcloud.ops.business.listing.controller.admin.vo.response;

import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "关键词原数据明细")
public class KeywordMetadataRespVO {

    private Long id;

    @Schema(description = "站点")
    private String market;

    @Schema(description = "站点 ID")
    private Long marketId;

    /**
     * 数据网址
     */
    @Schema(description = "站点 ID")
    private String website;

    /**
     * 关键词
     */
    @Schema(description = "站点 ID")
    private String keywords;

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
    private Double trafficPercentage;
    /**
     * 预估周曝光量
     */
    private Double calculatedWeeklySearches;

    private List<SearchesTrendDTO> trends;

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
    private List<MonopolyAsinDtosDTO>  monopolyAsinDtos;

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
    private List<String> badges;

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
    private DepartmentsDTO departments;
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
    private Long updatedTime;
}
