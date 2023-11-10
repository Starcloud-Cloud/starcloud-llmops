package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 卖家精灵管关键词反查 DTO
 */

public class KeyWordReversDTO {

    private Object guestIdX;
    private String stationX;
    private Integer marketId;
    private String website;
    private String asin;
    private Integer total;
    private List<ItemsDTO> items;
    private List<StatsDTO> stats;
    private Integer took;
    private Object unauthorized;
    private Boolean guestVisited;

    public static class ItemsDTO {
        private Object guestIdX;
        private String keywords;
        private String keywordCn;
        private String keywordJp;
        private Integer searches;
        private Integer products;
        private Integer purchases;
        private Double purchaseRate;
        private Double bid;
        private Double bidMax;
        private Double bidMin;
        private List<String> badges;
        private String position;
        private List<String> positions;
        private List<GkDatasDTO> gkDatas;
        private String top10Asin;
        private BaseDTO rankPosition;
        private Object adPosition;
        private Long updatedTime;
        private Integer searchesRank;
        private Long searchesRankTimeFrom;
        private Long searchesRankTimeTo;
        private Integer latest1daysAds;
        private Integer latest7daysAds;
        private Integer latest30daysAds;
        private Double supplyDemandRatio;
        private List<SearchesTrendDTO> searchesTrend;
        private Double trafficPercentage;
        private Object trafficKeywordTypes;
        private Object conversionKeywordTypes;
        private Double calculatedWeeklySearches;
        private List<ClickTop3sDTO> araClickTop3;
        private Integer titleDensityExact;
        private Integer cprExact;
        private Double avgPrice;
        private Integer avgReviews;
        private Double avgRating;
        private Object ac;
        private Double naturalRatio;
        private Double recommendRatio;
        private Double adRatio;
        private Double monopolyClickRate;
        private Double top3ClickingRate;
        private Double top3ConversionRate;
        private Boolean guestVisited;

    }

}