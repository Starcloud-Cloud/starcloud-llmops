package com.starcloud.ops.business.mission.api.vo.response;

import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingContentDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "单个任务")
public class AppSingleMissionRespVO {

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "任务类型")
    private String type;

    @Schema(description = "通告名称")
    private String notificationName;

    @Schema(description = "任务内容")
    private PostingContentDTO content;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "认领人Id")
    private String claimUserId;

    @Schema(description = "认领人")
    private String claimUsername;

    @Schema(description = "认领时间")
    private LocalDateTime claimTime;

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

    @Schema(description = "结算失败原因")
    private String errorMsg;

    @Schema(description = "点赞数")
    private Integer likedCount;

    @Schema(description = "收藏数")
    private Integer collectedCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "价格明细")
    private SingleMissionPostingPriceDTO unitPrice;

    @Schema(description = "预结算失败信息")
    private String preSettlementMsg;

    @Schema(description = "关闭信息")
    private String closeMsg;

    @Schema(description = "结算失败信息")
    private String settlementMsg;

}