package com.starcloud.ops.business.log.api.conversation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class LogAppMessageStatisticsListVO {

    @Schema(description = "apUid", example = "10286")
    private String appUid;

    @Schema(description = "appMode", example = "10286")
    private String appMode;

    @Schema(description = "appName", example = "10286")
    private String appName;

    @Schema(description = "执行场景", example = "10286")
    private String fromScene;


    @Schema(description = "消息总数", example = "10286")
    private Integer messageCount;

    @Schema(description = "用户总数", example = "10286")
    private Integer userCount;

    @Schema(description = "总耗时", example = "10286")
    private BigDecimal elapsedTotal;

    @Schema(description = "平均耗时", example = "10286")
    private BigDecimal elapsedAvg;


    @Schema(description = "请求tokens总数", example = "10286")
    private Integer messageTokens;


    @Schema(description = "回答tokens总数", example = "10286")
    private Integer answerTokens;


    @Schema(description = "tokens总数", example = "10286")
    private Integer tokens;


    @Schema(description = "状态", example = "10286")
    private String status;


    @Schema(description = "用户", example = "10286")
    private String creator;


    @Schema(description = "终端用户", example = "10286")
    private String endUser;


    @Schema(description = "生成时间", example = "10286")
    private String createDate;

}
