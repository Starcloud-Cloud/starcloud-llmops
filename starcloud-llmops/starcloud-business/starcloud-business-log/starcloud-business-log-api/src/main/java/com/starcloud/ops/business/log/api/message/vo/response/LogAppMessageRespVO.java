package com.starcloud.ops.business.log.api.message.vo.response;

import com.starcloud.ops.business.log.api.message.vo.LogAppMessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 */
@Schema(description = "管理后台 - 应用执行日志结果 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageRespVO extends LogAppMessageBaseVO {

    private static final long serialVersionUID = 2985878414067003444L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}