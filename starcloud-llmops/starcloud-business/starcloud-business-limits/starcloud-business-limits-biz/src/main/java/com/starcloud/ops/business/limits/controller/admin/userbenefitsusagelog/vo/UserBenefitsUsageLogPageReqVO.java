package com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户权益使用日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBenefitsUsageLogPageReqVO extends PageParam {

    @Schema(description = "策略编号")
    private String uid;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "操作类型(使用、过期、增加对应字典)")
    private String action;

    @Schema(description = "权益类型（例如：应用数、数据集数、图片数、Token数）", example = "1")
    private String benefitsType;

    @Schema(description = "权益数量变化（正数表示增加，负数表示减少）")
    private Long amount;

    @Schema(description = "应用程序ID或者数据集ID")
    private String outId;

    @Schema(description = "用户权益编号（单条权益不够，扣除其他策略下）")
    private String benefitsIds;

    @Schema(description = "使用时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] usageTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
