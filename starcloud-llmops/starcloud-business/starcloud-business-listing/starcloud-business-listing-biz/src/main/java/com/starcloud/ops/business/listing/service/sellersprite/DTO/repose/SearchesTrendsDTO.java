package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SearchesTrendDTO
 */
@NoArgsConstructor
@Data
public class SearchesTrendsDTO {
    private String month;
    private Integer search;
    private Integer searchRank;
}