package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "建议关键词")
public class DraftContentConfigDTO {

    @Schema(description = "不计入已使用")
    private Boolean ignoreUse;

    @Schema(description = "建议关键词")
    private List<String> recommendKeys;

}
