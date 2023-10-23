package com.starcloud.ops.business.listing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "草稿配置")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DraftConfigDTO {

    @Schema(description = "开启ai模式")
    private Boolean enableAi;

    // todo  ai 配置


    @Schema(description = "五点数量")
    private Integer fiveDescNum;
//

    @Schema(description = "标题配置")
    private DraftContentConfigDTO titleConfig;

    @Schema(description = "五点建议配置")
    private Map<String, DraftContentConfigDTO> fiveDescConfig;

    @Schema(description = "产品描述配置")
    private DraftContentConfigDTO productDescConfig;

    @Schema(description = "搜索词配置")
    private DraftContentConfigDTO searchTermConfig;

}
