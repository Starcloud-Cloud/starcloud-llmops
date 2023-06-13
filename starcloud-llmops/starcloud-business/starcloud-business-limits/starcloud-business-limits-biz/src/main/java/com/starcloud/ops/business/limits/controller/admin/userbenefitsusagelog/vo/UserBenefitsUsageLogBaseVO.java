package com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 用户权益使用日志 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class UserBenefitsUsageLogBaseVO {

    @Schema(description = "策略编号")
    private String uid;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "操作类型(使用、过期、增加对应字典)")
    private String action;

    @Schema(description = "权益类型（例如：应用数、数据集数、图片数、Token数）")
    private String benefitsType;

    @Schema(description = "权益数量变化（正数表示增加，负数表示减少）")
    private Long amount;

    @Schema(description = "应用程序ID或者数据集ID")
    private Long outId;

    @Schema(description = "用户权益编号（单条权益不够，扣除其他策略下）")
    private String benefitsIds;

    @Schema(description = "使用时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime usageTime;

}
