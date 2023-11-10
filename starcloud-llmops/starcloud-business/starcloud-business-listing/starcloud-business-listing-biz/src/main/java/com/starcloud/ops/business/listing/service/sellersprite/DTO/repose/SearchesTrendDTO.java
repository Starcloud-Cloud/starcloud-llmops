package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SearchesTrendDTO
 */
@NoArgsConstructor
@Data
public class SearchesTrendDTO {
    private String month;
    private Integer searches;
    private Integer searchRank;
}