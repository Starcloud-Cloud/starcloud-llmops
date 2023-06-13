package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 用户权益 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class UserBenefitsBaseVO {

    @Schema(description = "编号")
    private String uid;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "策略编号")
    private String strategyId;

    @Schema(description = "应用数")
    private Long appCountUsed;

    @Schema(description = "数据集数")
    private Long datasetCountUsed;

    @Schema(description = "图片数")
    private Long imageCountUsed;

    @Schema(description = "令牌数")
    private Long tokenCountUsed;

    @Schema(description = "生效时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime effectiveTime;

    @Schema(description = "过期时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime expirationTime;

}
