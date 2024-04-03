package com.starcloud.ops.business.app.controller.admin.comment.strategy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 媒体回复策略 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MediaStrategyRespVO {

    @Schema(description = "策略编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15957")
    @ExcelProperty("策略编号")
    private Long id;

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("策略名称")
    private String name;

    @Schema(description = "平台类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("平台类型")
    private Integer platformType;

    @Schema(description = "策略类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("策略类型")
    private Integer strategyType;

    @Schema(description = "关键词匹配", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("关键词匹配")
    private Integer keywordMatchType;

    @Schema(description = "关键词")
    @ExcelProperty("关键词")
    private String keywordGroups;

    @Schema(description = "具体操作")
    @ExcelProperty("具体操作")
    private String actions;

    @Schema(description = "时机")
    @ExcelProperty("时机")
    private Integer interval;

    @Schema(description = "频率")
    @ExcelProperty("频率")
    private Integer frequency;

    @Schema(description = "指定用户组")
    @ExcelProperty("指定用户组")
    private String assignAccountGroups;

    @Schema(description = "指定作品组")
    @ExcelProperty("指定作品组")
    private String assignMediaGroups;

    @Schema(description = "生效开始时间")
    @ExcelProperty("生效开始时间")
    private LocalDateTime validStartTime;

    @Schema(description = "生效结束时间")
    @ExcelProperty("生效结束时间")
    private LocalDateTime validEndTime;

    @Schema(description = "回复时间", example = "2")
    @ExcelProperty("回复时间")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}