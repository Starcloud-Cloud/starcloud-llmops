package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 支付订单提交 Response VO")
@Data
public class PayOrderSubmitRespVO {

    @Schema(description = "展示模式", required = true, example = "url") // 参见 PayDisplayModeEnum 枚举
    private String displayMode;

    @Schema(description = "展示内容", required = true)
    private String displayContent;

}
