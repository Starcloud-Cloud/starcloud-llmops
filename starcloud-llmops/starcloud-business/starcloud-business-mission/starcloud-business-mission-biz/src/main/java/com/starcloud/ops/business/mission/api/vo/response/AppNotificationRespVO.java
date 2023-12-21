package com.starcloud.ops.business.mission.api.vo.response;

import com.starcloud.ops.business.mission.controller.admin.vo.dto.ClaimLimitDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingUnitPriceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "通告详情")
public class AppNotificationRespVO {

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "通告名称")
    private String name;

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "领域")
    private String field;

    @Schema(description = "任务类型")
    private String type;

    @Schema(description = "状态")
    private String status;

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

    @Schema(description = "领取人员限制")
    private ClaimLimitDTO claimLimit;

    @Schema(description = "最小粉丝数")
    private Integer minFansNum ;

    @Schema(description = "总任务量")
    private Integer total;

    @Schema(description = "总领取数")
    private Integer claimCount;

    @Schema(description = "当前用户领取数量")
    private Integer currentUserNum;

    @Schema(description = "访问次数")
    private Integer visitNum;

    @Schema(description = "任务uid")
    private String messionUids;

    @Schema(description = "图片")
    private String picture;

    @Schema(description = "管理员详情")
    private UserDetailVO userDetail;

    @Schema(description = "是否可认领")
    private Boolean canClaim;
}
