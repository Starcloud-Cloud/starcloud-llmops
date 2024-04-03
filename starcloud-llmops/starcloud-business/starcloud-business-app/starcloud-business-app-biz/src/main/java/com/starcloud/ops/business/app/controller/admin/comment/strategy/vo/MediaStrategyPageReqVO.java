package com.starcloud.ops.business.app.controller.admin.comment.strategy.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 媒体回复策略分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MediaStrategyPageReqVO extends PageParam {

    @Schema(description = "策略名称", example = "芋艿")
    private String name;

    @Schema(description = "平台类型", example = "2")
    private Integer platformType;

    @Schema(description = "策略类型", example = "1")
    private Integer strategyType;

    @Schema(description = "关键词匹配", example = "1")
    private Integer keywordMatchType;

    @Schema(description = "关键词")
    private String keywordGroups;

    @Schema(description = "具体操作")
    private String actions;

    @Schema(description = "时机")
    private Integer interval;

    @Schema(description = "频率")
    private Integer frequency;

    @Schema(description = "指定用户组")
    private String assignAccountGroups;

    @Schema(description = "指定作品组")
    private String assignMediaGroups;

    @Schema(description = "生效开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] validStartTime;

    @Schema(description = "生效结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] validEndTime;

    @Schema(description = "回复时间", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}