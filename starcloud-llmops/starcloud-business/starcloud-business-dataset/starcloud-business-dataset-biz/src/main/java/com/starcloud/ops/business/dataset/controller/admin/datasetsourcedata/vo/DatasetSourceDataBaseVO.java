package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 数据集源数据 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class DatasetSourceDataBaseVO {

    @Schema(description = "编号", required = true)
    @NotNull(message = "编号不能为空")
    private String uid;

    @Schema(description = "名称", required = true)
    @NotNull(message = "名称不能为空")
    private String name;

    @Schema(description = "存储ID", example = "18574L")
    private Long storageId;

    @Schema(description = "位置", required = true)
    @NotNull(message = "位置不能为空")
    private Long position;

    @Schema(description = "数据源类型（0-本地上传，1-接口上传）", required = true)
    @NotNull(message = "数据源类型（0-本地上传，1-接口上传）不能为空")
    private Integer dataSourceType;

    @Schema(description = "数据源信息")
    private String dataSourceInfo;

    @Schema(description = "数据集处理规则ID")
    private String datasetProcessRuleId;

    @Schema(description = "批次", required = true)
    @NotNull(message = "批次不能为空")
    private String batch;

    @Schema(description = "创建来源", required = true)
    @NotNull(message = "创建来源不能为空")
    private String createdFrom;

    @Schema(description = "字数")
    private Long wordCount;

    @Schema(description = "令牌数")
    private Long tokens;

    @Schema(description = "数据集ID", required = true)
    @NotNull(message = "数据集ID不能为空")
    private String datasetId;

    @Schema(description = "创建API请求ID")
    private String createdApiRequestId;

    @Schema(description = "解析完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime parsingCompletedTime;

    @Schema(description = "清洗完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime cleaningCompletedTime;

    @Schema(description = "拆分完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime splittingCompletedTime;

    @Schema(description = "索引创建时间")
    private Double indexingTime;

    @Schema(description = "处理开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime processingStartedTime;

    @Schema(description = "完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime completedAt;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "停止时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime stoppedTime;

    @Schema(description = "暂停人")
    private String pausedBy;

    @Schema(description = "暂停时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime pausedTime;

    @Schema(description = "禁用人")
    private String disabledAt;

    @Schema(description = "禁用时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime disabledTime;


    @Schema(description = "索引状态", required = true, example = "1")
    @NotNull(message = "索引状态不能为空")
    private String indexingStatus;

    @Schema(description = "是否启用", required = true)
    @NotNull(message = "是否启用不能为空")
    private Boolean enabled;

    @Schema(description = "是否归档", required = true)
    @NotNull(message = "是否归档不能为空")
    private Boolean archived;

    @Schema(description = "归档人")
    private String archivedBy;

    @Schema(description = "归档原因", example = "不香")
    private String archivedReason;

    @Schema(description = "是否暂停")
    private Boolean isPaused;

    @Schema(description = "归档时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime archivedTime;

}