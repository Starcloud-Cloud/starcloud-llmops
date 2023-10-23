package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RelationVariationsItemsDTO {
    private String market;
    private String asin;
    private String imageUrl;
    private Double trafficPercentage;
    private String title;
    private Double price;
    private Integer reviews;
    private Double rating;
}