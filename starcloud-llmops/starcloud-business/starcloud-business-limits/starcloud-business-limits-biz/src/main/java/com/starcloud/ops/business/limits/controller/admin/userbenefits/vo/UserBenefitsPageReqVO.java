package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户权益分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBenefitsPageReqVO extends PageParam {

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
    private LocalDateTime[] effectiveTime;

    @Schema(description = "过期时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] expirationTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
