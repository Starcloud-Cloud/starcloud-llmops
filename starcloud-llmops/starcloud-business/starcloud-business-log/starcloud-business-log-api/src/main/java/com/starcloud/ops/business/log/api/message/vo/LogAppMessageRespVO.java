package com.starcloud.ops.business.log.api.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应用执行日志结果 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageRespVO extends LogAppMessageBaseVO {

    @Schema(description = "ID", required = true, example = "6654")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}