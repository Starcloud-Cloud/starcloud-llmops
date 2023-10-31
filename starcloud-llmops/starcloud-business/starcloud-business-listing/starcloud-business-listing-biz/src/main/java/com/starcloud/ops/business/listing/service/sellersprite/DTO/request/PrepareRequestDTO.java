package com.starcloud.ops.business.listing.service.sellersprite.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 流量词拓展 获取拓词方式
 */
@NoArgsConstructor
@Data
public class PrepareRequestDTO {

    @Schema(description = "时间")
    private String month;

    @Schema(description = "站点", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "站点不能为空")
    private Integer market;

    @Schema(description = " ASIN 集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "站点不能为空")
    private List<String> asinList;
}
