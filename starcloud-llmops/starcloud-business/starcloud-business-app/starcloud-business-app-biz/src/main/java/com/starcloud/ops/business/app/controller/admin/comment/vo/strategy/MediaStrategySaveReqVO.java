package com.starcloud.ops.business.app.controller.admin.comment.vo.strategy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 媒体回复策略新增/修改 Request VO")
@Data
public class MediaStrategySaveReqVO {

    @Schema(description = "策略编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15957")
    private Long id;

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "策略名称不能为空")
    private String name;

    @Schema(description = "平台类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "平台类型不能为空")
    private Integer platformType;

    @Schema(description = "策略类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "策略类型不能为空")
    private Integer actionType;

    @Schema(description = "关键词匹配", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关键词匹配不能为空")
    private Integer keywordMatchType;

    @Schema(description = "关键词")
    private List<String> keywordGroups;

    @Schema(description = "具体操作")
    private String actions;

    @Schema(description = "时机")
    private Integer intervalTimes;

    @Schema(description = "频率")
    private Integer frequency;

    @Schema(description = "指定用户组")
    private String assignAccount;

    @Schema(description = "指定作品组")
    private String assignMedia;

    @Schema(description = "生效开始时间")
    @DateTimeFormat(pattern = FORMAT_HOUR_MINUTE_SECOND)
    private LocalTime validStartTime;

    @Schema(description = "生效结束时间")
    @DateTimeFormat(pattern = FORMAT_HOUR_MINUTE_SECOND)
    private LocalTime validEndTime;

    @Schema(description = "回复时间", example = "2")
    private Integer status;

}