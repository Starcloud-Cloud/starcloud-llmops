package com.starcloud.ops.business.mission.controller.admin.vo.response;

import com.starcloud.ops.business.dto.PostingUnitPriceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "任务详情")
public class NotificationRespVO {

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "任务类型")
    private String type;

    @Schema(description = "单价明细")
    private PostingUnitPriceDTO unitPrice;

    @Schema(description = "任务开始时间")
    private LocalDateTime startTime;

    @Schema(description = "任务结束时间")
    private LocalDateTime endTime;

    @Schema(description = "通告总预算")
    private BigDecimal notificationBudget;

    @Schema(description = "单个任务预算")
    private BigDecimal singleBudget;

    @Schema(description = "任务说明")
    private String description;

    @Schema(description = "备注")
    private String remark;
}
