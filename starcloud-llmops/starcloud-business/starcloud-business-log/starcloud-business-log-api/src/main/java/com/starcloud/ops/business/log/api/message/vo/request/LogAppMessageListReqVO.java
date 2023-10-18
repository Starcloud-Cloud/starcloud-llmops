package com.starcloud.ops.business.log.api.message.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "管理后台 - 应用执行日志结果列表，参数和 LogAppMessagePageReqVO 是一致的")
public class LogAppMessageListReqVO implements Serializable {

    private static final long serialVersionUID = -450243518975444820L;

    /**
     * 消息uid
     */
    @Schema(description = "消息uid", example = "12463")
    private String uid;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", example = "28352")
    private String appConversationUid;

    /**
     * app uid
     */
    @Schema(description = "app uid", example = "24405")
    private String appUid;

    /**
     * app name
     */
    @Schema(description = "app 模式")
    private String appMode;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * ai model
     */
    @Schema(description = "ai model")
    private String aiModel;

    /**
     * 执行状态
     */
    @Schema(description = "执行状态，error：失败，success：成功")
    private String status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

}