package com.starcloud.ops.business.log.api.conversation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应用执行日志会话 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppConversationRespVO extends LogAppConversationBaseVO {

    @Schema(description = "ID", required = true, example = "13032")
    private Long id;

    @Schema(description = "模版创建时间")
    private LocalDateTime createTime;

}