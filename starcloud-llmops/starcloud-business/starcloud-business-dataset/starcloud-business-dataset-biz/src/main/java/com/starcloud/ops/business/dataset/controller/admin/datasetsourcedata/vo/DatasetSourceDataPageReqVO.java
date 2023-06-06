package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 数据集源数据分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetSourceDataPageReqVO extends PageParam {

    @Schema(description = "编号", example = "9732")
    private String uid;

    @Schema(description = "名称", example = "张三")
    private String name;

    @Schema(description = "存储ID", example = "18574")
    private String storageId;

    @Schema(description = "位置")
    private Integer position;

    @Schema(description = "数据源类型（0-本地上传，1-接口上传）", example = "1")
    private Integer dataSourceType;

    @Schema(description = "数据源信息")
    private String dataSourceInfo;

    @Schema(description = "数据集处理规则ID", example = "13802")
    private String datasetProcessRuleId;

    @Schema(description = "批次")
    private String batch;

    @Schema(description = "创建来源")
    private String createdFrom;

    @Schema(description = "字数", example = "20733")
    private Long wordCount;

    @Schema(description = "令牌数")
    private Long tokens;

    @Schema(description = "数据集ID", example = "26942")
    private String datasetId;

    @Schema(description = "创建API请求ID", example = "2458")
    private String createdApiRequestId;

    @Schema(description = "解析完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] parsingCompletedTime;

    @Schema(description = "清洗完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] cleaningCompletedTime;

    @Schema(description = "拆分完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] splittingCompletedTime;

    @Schema(description = "索引创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Object[] indexingTime;

    @Schema(description = "处理开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] processingStartedTime;

    @Schema(description = "完成时间")
    private LocalDateTime completedAt;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "停止时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] stoppedTime;

    @Schema(description = "暂停人")
    private String pausedBy;

    @Schema(description = "暂停时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] pausedTime;

    @Schema(description = "禁用人")
    private String disabledAt;

    @Schema(description = "禁用时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] disabledTime;


    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "索引状态", example = "1")
    private String indexingStatus;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "文档类型", example = "1")
    private String docType;

    @Schema(description = "文档元数据")
    private String docMetadata;

    @Schema(description = "是否归档")
    private Boolean archived;

    @Schema(description = "归档人")
    private String archivedBy;

    @Schema(description = "归档原因", example = "不香")
    private String archivedReason;

    @Schema(description = "是否暂停")
    private Boolean isPaused;

    @Schema(description = "归档时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] archivedTime;

}