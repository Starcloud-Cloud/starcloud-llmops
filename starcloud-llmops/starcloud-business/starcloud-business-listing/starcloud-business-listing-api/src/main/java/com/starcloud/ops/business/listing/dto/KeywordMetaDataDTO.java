package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "关键字数据")
public class KeywordMetaDataDTO {

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "月搜索量")
    private Long searches;

    @Schema(description = "月购买量")
    private Long purchases;

    @Schema(description = "月购买量比列")
    private Double purchaseRate;

}
