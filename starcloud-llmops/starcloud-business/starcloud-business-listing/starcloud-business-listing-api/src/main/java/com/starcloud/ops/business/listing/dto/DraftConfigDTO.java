package com.starcloud.ops.business.listing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "草稿配置")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DraftConfigDTO {

    @Schema(description = "开启ai模式")
    private Boolean enableAi;

    @Schema(description = "ai模式配置")
    private AiConfigDTO aiConfigDTO;

    @Schema(description = "五点数量")
    private Integer fiveDescNum = 5;

    @Schema(description = "标题配置")
    private DraftContentConfigDTO titleConfig;

    @Schema(description = "五点建议配置")
    private Map<String, DraftContentConfigDTO> fiveDescConfig;

    @Schema(description = "产品描述配置")
    private DraftContentConfigDTO productDescConfig;

    @Schema(description = "搜索词配置")
    private DraftContentConfigDTO searchTermConfig;

    @Schema(description = "隐藏已使用")
    private Boolean hiddenUsed;

}
