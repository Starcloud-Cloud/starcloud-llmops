package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ai模式配置")
public class AiConfigDTO {

    @Schema(description = "产品特征")
    private String productFeatures;

    @Schema(description = "客户特征")
    private String customerFeatures;

    @Schema(description = "品牌名称")
    private String productName;

    @Schema(description = "显示位置")
    private String nameLocation;

    @Schema(description = "语言")
    private String language;

    @Schema(description = "风格")
    private String style;
}
