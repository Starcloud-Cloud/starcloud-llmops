package com.starcloud.ops.business.trade.controller.admin.sign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 交易订单创建 Response VO")
@Data
public class AppTradeSignCreateRespVO {

    @Schema(description = "签约编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "签约订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long paySignId;

}
