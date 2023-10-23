package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GkDatasDTO
 */
@NoArgsConstructor
@Data
public class GkDatasDTO {


    private String station;
    private String asin;
    private Object asinImage;
    private Object bigAsinImage;
    private Object asinPrice;
    private Object asinReviews;
    private Object asinRating;
    private Object asinBrand;
    private String asinTitle;

    private String keyword;
    private Object categoryId;
    private Integer maxPage;
    private String asinUrl;

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
