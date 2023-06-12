package com.starcloud.ops.business.log.api.messagesave.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 应用执行日志结果保存更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageSaveUpdateReqVO extends LogAppMessageSaveBaseVO {

    @Schema(description = "ID", required = true, example = "8352")
    @NotNull(message = "ID不能为空")
    private Long id;

}