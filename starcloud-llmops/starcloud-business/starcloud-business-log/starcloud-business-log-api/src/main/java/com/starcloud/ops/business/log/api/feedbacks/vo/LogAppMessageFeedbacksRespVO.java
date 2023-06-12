package com.starcloud.ops.business.log.api.feedbacks.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应用执行日志结果反馈 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageFeedbacksRespVO extends LogAppMessageFeedbacksBaseVO {

    @Schema(description = "ID", required = true, example = "24547")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}