package com.starcloud.ops.business.log.api.messagesave.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应用执行日志结果保存 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageSaveRespVO extends LogAppMessageSaveBaseVO {

    @Schema(description = "ID", required = true, example = "8352")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}