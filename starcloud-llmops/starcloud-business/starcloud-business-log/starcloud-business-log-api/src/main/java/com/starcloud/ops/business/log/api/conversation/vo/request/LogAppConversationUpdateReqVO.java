package com.starcloud.ops.business.log.api.conversation.vo.request;

import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Schema(description = "管理后台 - 应用执行日志会话更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppConversationUpdateReqVO extends LogAppConversationBaseVO {

    private static final long serialVersionUID = 1918276355361205010L;

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

}