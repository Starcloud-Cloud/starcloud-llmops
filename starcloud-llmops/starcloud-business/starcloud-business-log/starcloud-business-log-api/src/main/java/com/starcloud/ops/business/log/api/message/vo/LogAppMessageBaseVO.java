package com.starcloud.ops.business.log.api.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import javax.validation.constraints.*;

/**
 * 应用执行日志结果 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class LogAppMessageBaseVO {

    @Schema(description = "消息uid", required = true, example = "12463")
    @NotNull(message = "消息uid不能为空")
    private String uid;

    @Schema(description = "会话ID", required = true, example = "28352")
    @NotNull(message = "会话ID不能为空")
    private String appConversationUid;

    @Schema(description = "app uid", required = true, example = "24405")
    @NotNull(message = "app uid不能为空")
    private String appUid;

    @Schema(description = "app 模式", required = true)
    @NotNull(message = "app 模式不能为空")
    private String appMode;

    @Schema(description = "app 配置", required = true)
    @NotNull(message = "app 配置不能为空")
    private String appConfig;

    @Schema(description = "执行的 app step", required = true)
    @NotNull(message = "执行的 app step不能为空")
    private String appStep;

    @Schema(description = "模版状态，0：失败，1：成功", required = true, example = "2")
    @NotNull(message = "模版状态，0：失败，1：成功不能为空")
    private Byte status;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "app 配置", required = true)
    @NotNull(message = "app 配置不能为空")
    private String variables;

    @Schema(description = "请求内容", required = true)
    @NotNull(message = "请求内容不能为空")
    private String message;

    @Schema(description = "消耗token数", required = true)
    @NotNull(message = "消耗token数不能为空")
    private Integer messageTokens;

    @Schema(description = "消耗token单位价格", required = true, example = "23618")
    @NotNull(message = "消耗token单位价格不能为空")
    private Long messageUnitPrice;

    @Schema(description = "返回内容", required = true)
    @NotNull(message = "返回内容不能为空")
    private String answer;

    @Schema(description = "消耗token数", required = true)
    @NotNull(message = "消耗token数不能为空")
    private Integer answerTokens;

    @Schema(description = "消耗token单位价格", required = true, example = "18453")
    @NotNull(message = "消耗token单位价格不能为空")
    private Long answerUnitPrice;

    @Schema(description = "执行耗时", required = true)
    @NotNull(message = "执行耗时不能为空")
    private Object elapsed;

    @Schema(description = "总消耗价格", required = true, example = "4382")
    @NotNull(message = "总消耗价格不能为空")
    private Long totalPrice;

    @Schema(description = "价格单位", required = true)
    @NotNull(message = "价格单位不能为空")
    private String currency;

    @Schema(description = "执行场景", required = true)
    @NotNull(message = "执行场景不能为空")
    private String fromScene;

    @Schema(description = "临时用户ID")
    private String endUser;

}