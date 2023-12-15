package com.starcloud.ops.business.mission.controller.admin.vo.request;

import com.starcloud.ops.business.mission.controller.admin.vo.dto.ClaimLimitDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingUnitPriceDTO;
import com.starcloud.ops.business.enums.MisssionTypeEnum;
import com.starcloud.ops.business.enums.NotificationPlatformEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "修改通告")
public class NotificationModifyReqVO {

    @Schema(description = "uid")
    @NotBlank(message = "uid不能为空")
    private String uid;

    @Schema(description = "任务名称")
    @Length(max = 32, message = "任务名称长度不能大于 32 位")
    private String name;

    @Schema(description = "平台")
    @InEnum(value = NotificationPlatformEnum.class, field = InEnum.EnumField.CODE, message = "平台类型[{value}]必须是: {values}")
    private String platform;

    @Schema(description = "领域")
    @NotBlank(message = "领域不能为空")
    private String field;

    @Schema(description = "任务类型")
    @InEnum(value = MisssionTypeEnum.class, field = InEnum.EnumField.CODE, message = "任务类型[{value}]必须是: {values}")
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

    @Schema(description = "领取人员限制")
    private ClaimLimitDTO claimLimit;
}
