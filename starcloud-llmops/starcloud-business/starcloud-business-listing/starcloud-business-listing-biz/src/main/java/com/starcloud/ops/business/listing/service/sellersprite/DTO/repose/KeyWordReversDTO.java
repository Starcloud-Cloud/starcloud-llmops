package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 卖家精灵管关键词反查 DTO
 */
@NoArgsConstructor
@Data
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

    @NoArgsConstructor
    @Data
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
        private RankPositionDTO rankPosition;
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
        private List<AraClickTop3DTO> araClickTop3;
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

        @NoArgsConstructor
        @Data
        public static class RankPositionDTO {
            private Integer page;
            private Integer pageSize;
            private Integer index;
            private Integer position;
            private Long updatedTime;
        }

        @NoArgsConstructor
        @Data
        public static class GkDatasDTO {
            private String stationX;
            private String keywordX;
            private Object categoryId;
            private Integer maxPage;
            private String asin;
            private String asinUrl;
            private String asinImage;
            private Object bigAsinImage;
            private Double asinPrice;
            private Integer asinReviews;
            private Double asinRating;
            private Object asinBrand;
            private String asinTitle;
            private Object rank;
            private Integer rankPage;
            private Integer rankPagesize;
            private Integer rankIndex;
            private Integer position;
            private Integer products;
            private Object sku;
            private Object maxRankPage;
            private Object ad;
            private Object amazonChoice;
            private String badges;
        }

        @NoArgsConstructor
        @Data
        public static class SearchesTrendDTO {
            private String month;
            private Integer searches;
            private Integer searchRank;
        }

        @NoArgsConstructor
        @Data
        public static class AraClickTop3DTO {
            private String asin;
            private String imageUrl;
            private Double clickRate;
            private Double conversionRate;
            private Object productTitle;
        }
    }

    @NoArgsConstructor
    @Data
    public static class StatsDTO {
        private String keywords;
        private Integer total;
        private Integer level;
    }

}