package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo;


import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户权益策略表 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBenefitsStrategyPageReqVO extends PageParam {

    @Schema(description = "策略编号")
    private String uid;

    @Schema(description = "兑换码")
    private String code;

    @Schema(description = "策略名称")
    private String strategyName;

    @Schema(description = "策略描述")
    private String strategyDesc;

    @Schema(description = "策略类型（字典中管理）")
    private String strategyType;

    @Schema(description = "应用数")
    private Long appCount;

    @Schema(description = "数据集数")
    private Long datasetCount;

    @Schema(description = "图片数")
    private Long imageCount;

    @Schema(description = "令牌数")
    private Long tokenCount;

    @Schema(description = "适用范围（字典中管理）")
    private String scope;

    @Schema(description = "适用时间")
    private Integer scopeNum;

    @Schema(description = "限制兑换次数（-1，不设限制）")
    private Long limitUnit;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "是否归档")
    private Boolean archived;

    @Schema(description = "归档人")
    private String archivedBy;

    @Schema(description = "归档时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] archivedTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

