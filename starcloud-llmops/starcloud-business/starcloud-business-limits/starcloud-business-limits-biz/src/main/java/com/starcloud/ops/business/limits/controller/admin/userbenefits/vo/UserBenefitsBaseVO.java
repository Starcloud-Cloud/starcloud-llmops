package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 用户权益 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class UserBenefitsBaseVO {

    @Schema(description = "策略ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "策略ID不能为空")
    private String strategyId;

    @Schema(description = "生效时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "生效时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime effectiveTime;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "过期时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime expirationTime;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否启用不能为空")
    private Boolean enabled;

    @Schema(description = "策略编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "策略编号不能为空")
    private String uid;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private String userId;

    @Schema(description = "可使用应用数")
    private Long appCountUsed;

    @Schema(description = "可使用数据集数")
    private Long datasetCountUsed;

    @Schema(description = "可使用图片数")
    private Long imageCountUsed;

    @Schema(description = "可使用令牌数")
    private Long tokenCountUsed;

    @Schema(description = "赠送令牌数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "赠送令牌数不能为空")
    private Long tokenCountInit;

    @Schema(description = "赠送图片数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "赠送图片数不能为空")
    private Long imageCountInit;

    @Schema(description = "赠送应用数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "赠送应用数不能为空")
    private Long datasetCountInit;

    @Schema(description = "赠送应用数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "赠送应用数不能为空")
    private Long appCountInit;

}
