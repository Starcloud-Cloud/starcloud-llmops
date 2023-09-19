package com.starcloud.ops.business.log.api.conversation.vo.response;

import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 */
@Schema(description = "管理后台 - 应用执行日志会话 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class LogAppConversationRespVO extends LogAppConversationBaseVO {

    private static final long serialVersionUID = -2397518883869203883L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "模版创建时间")
    private LocalDateTime createTime;

}