package com.starcloud.ops.business.log.api.conversation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 应用执行日志会话 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class LogAppConversationBaseVO {

    @Schema(description = "会话uid", required = true, example = "10286")
    @NotNull(message = "会话uid不能为空")
    private String uid;

    @Schema(description = "app uid", required = true, example = "24921")
    @NotNull(message = "app uid不能为空")
    private String appUid;

    @Schema(description = "app name", required = true, example = "24921")
    @NotNull(message = "app name不能为空")
    private String appName;

    @Schema(description = "app 模式", required = true)
    @NotNull(message = "app 模式不能为空")
    private String appMode;

    @Schema(description = "app 配置", required = true)
    @NotNull(message = "app 配置不能为空")
    private String appConfig;

    @Schema(description = "执行状态，error：失败，success：成功", required = true, example = "2")
    @NotNull(message = "执行状态，error：失败，success：成功不能为空")
    private String status;

    @Schema(description = "执行场景", required = true)
    @NotNull(message = "执行场景不能为空")
    private String fromScene;

    @Schema(description = "终端用户ID")
    private String endUser;

}