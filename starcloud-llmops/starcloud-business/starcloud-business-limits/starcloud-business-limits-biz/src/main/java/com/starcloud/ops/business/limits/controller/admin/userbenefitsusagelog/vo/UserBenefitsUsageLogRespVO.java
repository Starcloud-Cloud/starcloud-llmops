package com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户权益使用日志 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBenefitsUsageLogRespVO extends UserBenefitsUsageLogBaseVO {

    @Schema(description = "主键ID", required = true)
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
