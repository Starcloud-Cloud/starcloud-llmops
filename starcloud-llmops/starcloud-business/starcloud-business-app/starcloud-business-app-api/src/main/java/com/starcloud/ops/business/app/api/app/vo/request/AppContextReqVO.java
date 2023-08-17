package com.starcloud.ops.business.app.api.app.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author admin
 * @version 1.0.0
 * @since 2023-06-14
 */
@Data
public class AppContextReqVO implements Serializable {

    private static final long serialVersionUID = -1228480257988580816L;

    /**
     * 执行场景
     */
    @Schema(description = "场景")
    @NotNull(message = "场景Code 不能为空")
    private String scene;

    /**
     * 应用 UID
     */
    @Schema(description = "应用ID")
    private String appUid;

    /**
     * 渠道媒介 UID
     */
    @Schema(description = "渠道ID")
    private String mediumUid;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 游客的唯一标识
     */
    @Schema(description = "游客的唯一标识")
    private String endUser;

    /**
     * 会话UID
     */
    @Schema(description = "会话id")
    private String conversationUid;

    /**
     * 消息UID
     */
    @Schema(description = "消息ID")
    private String messageUid;

}
