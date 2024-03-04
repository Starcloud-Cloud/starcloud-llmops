package com.starcloud.ops.business.listing.controller.admin.vo.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 拓展 ASIN 导出 VO
 */
@Data
public class ExtendAsinReposeExcelVO {

    @ExcelProperty("关键词")
    private String keywords;

    @ExcelProperty("关键词-中文")
    private String keywordCn;

    @ExcelProperty("流量占比")
    private Double trafficPercentage;

    @ExcelProperty("预估周曝光量")
    private Double calculatedWeeklySearches;


    /**
     * 相关 ASIN
     */
    @ExcelProperty("相关 ASIN")
    private String relationVariationsItem;

    @ExcelProperty("ABA 周排名")
    private Long searchesRank;

    @ExcelProperty("日搜索量")
    private Long searchesDays;

    @ExcelProperty("月搜索量")
    private Long searches;

    @ExcelProperty("月购买量")
    private Long purchases;

    @ExcelProperty("月购买量比列")
    private Double purchaseRate;

    @ExcelProperty("SPR")
    private Long cprExact;

    @ExcelProperty("标题密度")
    private Long titleDensityExact;

    @ExcelProperty("商品数")
    private Long products;

    @ExcelProperty("供需比")
    private Double supplyDemandRatio;


    /**
     * 广告竞品数
     */
    @ExcelProperty("1 天内广告竞品数")
    private Long latest1daysAds;

    @ExcelProperty("7 天内广告竞品数")
    private Long latest7daysAds;
    @ExcelProperty("30 天内广告竞品数")
    private Long latest30daysAds;

    @ExcelProperty("前三 ASIN")
    private String clickTop3s;


    @ExcelProperty("点击集中度")
    private Double top3ClickingRate;

    @ExcelProperty("前三ASIN转化总占比")
    private Double top3ConversionRate;


    @ExcelProperty("PPC 竞价")
    private Double bid;
    /**
     * PPC 竞价 范围最小值
     */
    @ExcelProperty("PPC 竞价 范围最小值")
    private Double bidMin;

    /**
     * PPC 竞价 范围最大值
     */
    @ExcelProperty("PPC 竞价 范围最大值")
    private Double bidMax;

    /**
     * 搜索流量词
     */
    @ExcelProperty("搜索流量词")
    private String badges;


}
