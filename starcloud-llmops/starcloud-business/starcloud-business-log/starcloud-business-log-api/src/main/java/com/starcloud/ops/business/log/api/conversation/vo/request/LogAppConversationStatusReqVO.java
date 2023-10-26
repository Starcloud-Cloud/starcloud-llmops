package com.starcloud.ops.business.log.api.conversation.vo.request;

import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * 应用会话状态更新请求 VO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-19
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "LogAppConversationStatusReqVO", description = "应用会话状态更新请求 VO")
public class LogAppConversationStatusReqVO implements java.io.Serializable {

    private static final long serialVersionUID = -7251151853735378883L;

    /**
     * 会话 uid
     */
    @Schema(description = "会话uid")
    @NotEmpty(message = "会话 Uid 不能为空")
    private String uid;

    /**
     * AI 模型
     */
    @Schema(description = "AI模型")
    private String aiModel;

    /**
     * 会话状态
     */
    @Schema(description = "会话状态")
    @NotEmpty(message = "会话状态不能为空")
    @InEnum(value = LogStatusEnum.class, field = InEnum.EnumField.NAME, message = "会话状态[{value}], 不在合法范围内, 有效值：{values}")
    private String status;

    /**
     * 会话错误码
     */
    @Schema(description = "会话错误码")
    private String errorCode;

    /**
     * 会话错误信息
     */
    @Schema(description = "会话错误信息")
    private String errorMsg;
}
