package com.starcloud.ops.business.log.api.conversation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 应用执行日志会话 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 *
 * @author nacoyer
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class LogAppConversationBaseVO implements Serializable {

    private static final long serialVersionUID = 7104048190080172356L;

    /**
     * 会话 UID
     */
    @Schema(description = "会话uid")
    @NotNull(message = "会话uid不能为空")
    private String uid;

    /**
     * app uid
     */
    @Schema(description = "app uid")
    @NotNull(message = "app uid不能为空")
    private String appUid;

    /**
     * app name
     */
    @Schema(description = "app name")
    @NotNull(message = "app name不能为空")
    private String appName;

    /**
     * app 模式
     */
    @Schema(description = "app 模式")
    @NotNull(message = "app 模式不能为空")
    private String appMode;

    /**
     * app 配置
     */
    @Schema(description = "app 配置")
    @NotNull(message = "app 配置不能为空")
    private String appConfig;

    /**
     * 执行状态，error：失败，success：成功
     */
    @Schema(description = "执行状态，error：失败，success：成功")
    @NotNull(message = "执行状态，error：失败，success：成功不能为空")
    private String status;

    /**
     * 执行错误码
     */
    @Schema(description = "执行错误码")
    private String errorCode;

    /**
     * 执行错误信息
     */
    @Schema(description = "执行错误信息")
    private String errorMsg;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    @NotNull(message = "执行场景不能为空")
    private String fromScene;

    /**
     * 终端用户ID
     */
    @Schema(description = "终端用户ID")
    private String endUser;

}