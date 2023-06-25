package com.starcloud.ops.business.log.api.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 应用执行日志结果 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class LogAppMessageInfoRespVO {

    @Schema(description = "消息uid", required = true, example = "12463")
    private String uid;

    @Schema(description = "会话ID", required = true, example = "28352")
    private String appConversationUid;

    @Schema(description = "app uid", required = true, example = "24405")
    private String appUid;

    @Schema(description = "执行的 app step", required = true)
    private String appStep;

    @Schema(description = "执行状态，error：失败，success：成功", required = true, example = "2")
    private String status;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;


    @Schema(description = "请求内容", required = true)
    private String message;

    @Schema(description = "消耗token数", required = true)
    private Integer messageTokens;


    @Schema(description = "返回内容", required = true)
    private String answer;

    @Schema(description = "消耗token数", required = true)
    private Integer answerTokens;


    @Schema(description = "执行耗时", required = true)
    private Long elapsed;


    @Schema(description = "执行场景", required = true)
    private String fromScene;


    @Schema(description = "用户ID")
    private String user;

    @Schema(description = "临时用户ID")
    private String endUser;

}