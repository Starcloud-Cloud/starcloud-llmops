package com.starcloud.ops.business.listing.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "查询词库关键词")
public class DictKeyPageReqVO extends QueryKeywordMetadataPageReqVO{

    @Schema(description = "词库Uid")
    @NotBlank(message = "词库Uid不能为空")
    private String dictUid;
}
