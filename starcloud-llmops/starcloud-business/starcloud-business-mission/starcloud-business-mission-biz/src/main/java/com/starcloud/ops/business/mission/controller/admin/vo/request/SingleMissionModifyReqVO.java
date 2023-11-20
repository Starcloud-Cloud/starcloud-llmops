package com.starcloud.ops.business.mission.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "修改单条任务")
public class SingleMissionModifyReqVO {

    @Schema(description = "uid")
    @NotBlank(message = "uid 不能为空")
    private String uid;

    @Schema(description = "发布链接")
    private String publishUrl;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "预结算时间")
    private LocalDateTime preSettlementTime;

    @Schema(description = "预估花费")
    private BigDecimal estimatedAmount;

    @Schema(description = "结算时间")
    private LocalDateTime settlementTime;

    @Schema(description = "结算金额")
    private BigDecimal settlementAmount;

    @Schema(description = "支付单号")
    private String paymentOrder;

}
