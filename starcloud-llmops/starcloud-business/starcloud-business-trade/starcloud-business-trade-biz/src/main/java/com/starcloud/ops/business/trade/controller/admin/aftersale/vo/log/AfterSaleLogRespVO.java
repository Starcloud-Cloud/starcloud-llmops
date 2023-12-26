package com.starcloud.ops.business.trade.controller.admin.aftersale.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 交易售后日志 Response VO")
@Data
public class AfterSaleLogRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20669")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "22634")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @Schema(description = "售后编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "3023")
    @NotNull(message = "售后编号不能为空")
    private Long afterSaleId;

    @Schema(description = "售后状态（之前）", example = "2")
    private Integer beforeStatus;

    @Schema(description = "售后状态（之后）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "售后状态（之后）不能为空")
    private Integer afterStatus;

    @Schema(description = "操作明细", requiredMode = Schema.RequiredMode.REQUIRED, example = "维权完成，退款金额：¥37776.00")
    @NotNull(message = "操作明细不能为空")
    private String content;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
