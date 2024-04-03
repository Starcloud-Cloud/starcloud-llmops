package com.starcloud.ops.business.app.controller.admin.comment.strategy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

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
    private Integer strategyType;

    @Schema(description = "关键词匹配", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关键词匹配不能为空")
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
    private LocalDateTime validStartTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime validEndTime;

    @Schema(description = "回复时间", example = "2")
    private Integer status;

}