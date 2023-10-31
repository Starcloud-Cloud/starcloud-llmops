package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "站点")
    private String station;

    @Schema(description = " ASIN")
    private Object asin;

    @Schema(description = "asin 集合")
    private List<String> asinList;

    @Schema(description = "总数")
    private Integer total;

    @Schema(description = "关键词数据")
    private List<ItemsDTO> items;

    @Schema(description = "状态")
    private Object stats;

    @Schema(description = "未知")
    private Integer took;

    @Schema(description = "排序")
    private Integer sortTook;

    @Schema(description = "")
    private Integer varTook;

    @Schema(description = "")
    private Integer keywordTook;

    @Schema(description = "站点 ID")
    private Integer marketId;

    @Schema(description = "数据网址")
    private String website;
}
