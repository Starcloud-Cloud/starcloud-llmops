package com.starcloud.ops.business.log.api.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 应用执行日志结果更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageUpdateReqVO extends LogAppMessageBaseVO {

    @Schema(description = "ID", required = true, example = "6654")
    @NotNull(message = "ID不能为空")
    private Long id;

}