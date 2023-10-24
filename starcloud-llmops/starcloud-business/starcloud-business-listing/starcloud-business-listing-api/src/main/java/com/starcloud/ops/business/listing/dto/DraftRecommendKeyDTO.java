package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "建议关键词")
public class DraftRecommendKeyDTO {

    @Schema(description = "关键词")
    private String keyword;

//    @Schema(description = "推荐值")
//    private Double recommendValue;
}
