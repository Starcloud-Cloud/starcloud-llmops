package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "关键字元数据")
public class KeywordMetaDataDTO {

    @Schema(description = "元数据id")
    private Long id;

    @Schema(description = "关键词")
    private String keyword;


    // todo

}
