package com.starcloud.ops.business.mission.controller.admin.vo.response;

import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingUnitPriceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "任务详情")
public class NotificationRespVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "通告名称")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "总量")
    private Long total;

    @Schema(description = "待领取数")
    private Long stayClaimCount;

    @Schema(description = "领取数")
    private Long claimCount;

    @Schema(description = "用户发布数")
    private Long publishedCount;

    @Schema(description = "结算数")
    private Long settlementCount;

    @Schema(description = "创建人")
    private String createUser;
}
