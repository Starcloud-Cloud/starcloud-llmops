package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "内容配置")
public class DraftContentConfigDTO {

    @Schema(description = "不计入已使用")
    private Boolean ignoreUse = false;

    @Schema(description = "建议关键词")
    private List<DraftRecommendKeyDTO> recommendKeys;

}
