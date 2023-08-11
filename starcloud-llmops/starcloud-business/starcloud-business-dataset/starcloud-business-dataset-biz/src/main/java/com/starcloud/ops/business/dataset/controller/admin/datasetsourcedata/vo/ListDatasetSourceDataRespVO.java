package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 数据集源数据 Response VO")
@Data
@ToString(callSuper = true)
public class ListDatasetSourceDataRespVO {

    @Schema(description = "编号", required = true)
    private String uid;

    @Schema(description = "名称", required = true)
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "总结内容")
    private String summary;

    /**
     * 数据模型（0-问答，1-文档）
     */
    @Schema(description = "数据模型 （0-问答，1-文档）", required = true)
    private  Integer dataModel;
    /**
     * 数据源类型 文档 URL 字符串
     */
    @Schema(description = "数据源类型 文档 URL 字符串", required = true)
    private  String dataType;

    @Schema(description = "批次", required = true)
    private String batch;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "字数")
    private Long wordCount;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime updateTime;


}