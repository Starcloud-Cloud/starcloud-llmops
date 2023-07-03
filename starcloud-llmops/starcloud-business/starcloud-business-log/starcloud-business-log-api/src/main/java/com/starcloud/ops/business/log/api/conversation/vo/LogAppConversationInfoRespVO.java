package com.starcloud.ops.business.log.api.conversation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class LogAppConversationInfoRespVO implements Serializable {

    @Schema(description = "会话uid", required = true, example = "10286")
    private String uid;

    @Schema(description = "app uid", required = true, example = "24921")
    private String appUid;

    @Schema(description = "app 模式", required = true)
    private String appMode;

    @Schema(description = "app 名称", required = true)
    private String appName;


    @Schema(description = "执行场景", required = true)
    private String fromScene;


    @Schema(description = "请求总消耗token数")
    private Integer totalMessageTokens;

    @Schema(description = "返回总消耗token数")
    private Integer totalAnswerTokens;

    @Schema(description = "消息总数")
    private Integer messageCount;

    @Schema(description = "反馈总数")
    private Integer feedbacksCount;


    @Schema(description = "执行总耗时")
    private BigDecimal totalElapsed;

    @Schema(description = "执行总花费")
    private BigDecimal totalPrice;


    @Schema(description = "执行状态，error：失败，success：成功", example = "success")
    private String status;


    @Schema(description = "注册用户ID")
    private String creator;

    @Schema(description = "终端用户ID")
    private String endUser;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;


}