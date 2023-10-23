package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ExtendAsinReposeDTO
 */
@NoArgsConstructor
@Data
public class ExtendAsinReposeDTO {
    /**
     * 站点
     */
    private String station;

    private Object asin;

    private List<String> asinList;
    private Integer total;
    private List<ItemsDTO> items;
    private Object stats;
    private Integer took;
    private Integer sortTook;
    private Integer varTook;
    private Integer keywordTook;
    private Integer marketId;
    private String website;
}
