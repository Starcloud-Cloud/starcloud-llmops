package com.starcloud.ops.business.mission.controller.admin.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.starcloud.ops.business.dto.PostingUnitPriceDTO;
import com.starcloud.ops.business.enums.MisssionTypeEnum;
import com.starcloud.ops.business.enums.NotificationPlatformEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.*;

@Data
@Schema(description = "创建任务")
public class NotificationCreateReqVO {

    @Schema(description = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    @Length(max = 32, message = "任务名称长度不能大于 32 位")
    private String name;

    @Schema(description = "平台")
    @NotBlank(message = "任务平台不能为空")
    @InEnum(value = NotificationPlatformEnum.class, field = InEnum.EnumField.CODE, message = "平台类型[{value}]必须是: {values}")
    private String platform;

    @Schema(description = "领域")
    @NotBlank(message = "领域不能为空")
    private String field;

    @Schema(description = "任务类型")
    @InEnum(value = MisssionTypeEnum.class, field = InEnum.EnumField.CODE, message = "任务类型[{value}]必须是: {values}")
    @NotBlank(message = "任务类型不能为空")
    private String type;

    @Schema(description = "单价明细")
    @NotNull(message = "单价明细不能为空")
    private PostingUnitPriceDTO unitPrice;

    @Schema(description = "任务开始时间")
    @NotNull(message = "任务开始时间不能为空")
    private String startTime;

    @Schema(description = "任务结束时间")
    @NotNull(message = "任务结束时间不能为空")
    private String endTime;

    @Schema(description = "通告总预算")
    @NotNull(message = "通告总预算不能为空")
    @Min(value = 0, message = "通告总预算要大于0")
    private BigDecimal notificationBudget;

    @Schema(description = "单个任务预算")
    @NotNull(message = "单个任务预算不能为空")
    @Min(value = 0, message = "单个任务预算要大于0")
    private BigDecimal singleBudget;

    @Schema(description = "任务说明")
    @NotBlank(message = "任务说明不能为空")
    private String description;

    @Schema(description = "备注")
    private String remark;
}