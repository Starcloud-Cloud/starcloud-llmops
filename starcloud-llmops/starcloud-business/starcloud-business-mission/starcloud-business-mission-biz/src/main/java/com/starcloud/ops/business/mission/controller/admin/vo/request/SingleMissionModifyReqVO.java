package com.starcloud.ops.business.mission.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants.XHS_URL_REGEX;

@Data
@Schema(description = "修改单条任务")
public class SingleMissionModifyReqVO {

    @Schema(description = "uid")
    @NotBlank(message = "uid 不能为空")
    private String uid;

    @Schema(description = "认领Id")
    private String claimId;

    @Schema(description = "认领人")
    private String claimName;

    @Schema(description = "认领时间")
    private LocalDateTime claimTime;

    @Schema(description = "发布链接", example ="https://www.xiaohongshu.com/explore/24位数字和字母")
    @Pattern(regexp = XHS_URL_REGEX, message = "发布链接为浏览器访问地址，如： https://www.xiaohongshu.com/explore/24位数字和字母")
    private String publishUrl;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "点赞数")
    private Integer likedCount;

    @Schema(description = "评论数")
    private Integer commentCount;

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

    @Schema(description = "状态")
    private String status;

    @Schema(description = "定时执行时间")
    private LocalDateTime runTime;

    private String errorMsg;

}
