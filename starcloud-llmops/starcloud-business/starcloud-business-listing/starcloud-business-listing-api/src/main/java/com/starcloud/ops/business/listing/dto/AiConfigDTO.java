package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "ai模式配置")
public class AiConfigDTO {

    @Schema(description = "产品特征")
    private String productFeature;

    @Schema(description = "客户特征")
    private String customerFeature;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "显示位置")
    private String brandNameLocation;

    @Schema(description = "语言")
    private String targetLanguage;

    @Schema(description = "风格")
    private String writingStyle;

    @Schema(description = "商品属性")
    private List<String> commodityAttributes;

    @Schema(description = "AI模型")
    private String aiModel;

}
