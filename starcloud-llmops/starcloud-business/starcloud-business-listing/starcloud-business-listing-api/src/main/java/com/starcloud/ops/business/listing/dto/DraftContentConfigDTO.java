package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Schema(description = "内容配置")
public class DraftContentConfigDTO {

    @Schema(description = "不计入已使用")
    private Boolean ignoreUse = false;

    @Schema(description = "建议关键词")
    private List<DraftRecommendKeyDTO> recommendKeys;


    public List<String> getKeys() {
        if (recommendKeys == null) {
            return Collections.emptyList();
        }
        return recommendKeys.stream().map(DraftRecommendKeyDTO::getKeyword).collect(Collectors.toList());
    }

}
