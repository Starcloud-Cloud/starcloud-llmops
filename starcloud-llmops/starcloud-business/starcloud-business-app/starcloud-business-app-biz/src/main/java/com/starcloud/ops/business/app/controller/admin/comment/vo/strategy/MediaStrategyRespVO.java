package com.starcloud.ops.business.app.controller.admin.comment.vo.strategy;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Integer intervalTimes;

    @Schema(description = "频率")
    @ExcelProperty("频率")
    private Integer frequency;

    @Schema(description = "指定用户组")
    @ExcelProperty("指定用户组")
    private String assignAccount;

    @Schema(description = "指定作品组")
    @ExcelProperty("指定作品组")
    private String assignMedia;

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


    @Schema(description = "匹配数量", example = "2")
    @ExcelProperty("匹配数量")
    private Integer matchNum;

}