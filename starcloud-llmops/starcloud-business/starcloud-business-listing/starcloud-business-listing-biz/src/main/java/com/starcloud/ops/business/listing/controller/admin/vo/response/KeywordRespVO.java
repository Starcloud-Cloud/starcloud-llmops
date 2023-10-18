package com.starcloud.ops.business.listing.controller.admin.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "关键词明细")
public class KeywordRespVO {

    private Long id;

    @Schema(description = "词库Uid")
    private String dictUid;

    @Schema(description = "关键词")
    private String keywordResume;

    @Schema(description = "搜索量")
    private Integer searchVolume;
}
