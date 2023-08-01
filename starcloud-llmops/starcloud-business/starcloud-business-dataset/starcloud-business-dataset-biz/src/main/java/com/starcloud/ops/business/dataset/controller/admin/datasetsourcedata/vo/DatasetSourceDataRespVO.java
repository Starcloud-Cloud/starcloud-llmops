package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 数据集源数据 Response VO")
@Data
@ToString(callSuper = true)
public class DatasetSourceDataRespVO {

    @Schema(description = "主键ID", required = true, example = "4784")
    private Long id;

    @Schema(description = "编号", required = true)
    @NotNull(message = "编号不能为空")
    private String uid;

    @Schema(description = "名称", required = true)
    @NotNull(message = "名称不能为空")
    private String name;

    @Schema(description = "位置", required = true)
    @NotNull(message = "位置不能为空")
    private Long position;

    /**
     * 数据模型（0-问答，1-文档）
     */
    @Schema(description = "数据模型 （0-问答，1-文档）", required = true)
    @NotNull(message = "位置不能为空")
    private  Integer dataModel;
    /**
     * 数据源类型 文档 URL 字符串
     */
    @Schema(description = "数据源类型 文档 URL 字符串", required = true)
    @NotNull(message = "数据源类型不能为空")
    private  String dataType;

    @Schema(description = "批次", required = true)
    @NotNull(message = "批次不能为空")
    private String batch;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "字数")
    private Long wordCount;

    @Schema(description = "令牌数")
    private Long tokens;




}