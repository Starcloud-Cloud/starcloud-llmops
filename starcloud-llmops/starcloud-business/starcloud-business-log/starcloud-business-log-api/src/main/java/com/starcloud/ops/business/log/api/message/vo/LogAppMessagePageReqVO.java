package com.starcloud.ops.business.log.api.message.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 应用执行日志结果分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessagePageReqVO extends PageParam {

    @Schema(description = "消息uid", example = "12463")
    private String uid;

    @Schema(description = "会话ID", example = "28352")
    private String appConversationUid;

    @Schema(description = "app uid", example = "24405")
    private String appUid;

    @Schema(description = "app 模式")
    private String appMode;

    @Schema(description = "app 配置")
    private String appConfig;

    @Schema(description = "执行的 app step")
    private String appStep;

    @Schema(description = "执行状态，error：失败，success：成功", required = true, example = "2")
    private String status;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "app 配置")
    private String variables;

    @Schema(description = "请求内容")
    private String message;

    @Schema(description = "消耗token数")
    private Integer messageTokens;

    @Schema(description = "消耗token单位价格", example = "23618")
    private Long messageUnitPrice;

    @Schema(description = "返回内容")
    private String answer;

    @Schema(description = "消耗token数")
    private Integer answerTokens;

    @Schema(description = "消耗token单位价格", example = "18453")
    private Long answerUnitPrice;

    @Schema(description = "执行耗时")
    private Long elapsed;

    @Schema(description = "总消耗价格", example = "4382")
    private Long totalPrice;

    @Schema(description = "价格单位")
    private String currency;

    @Schema(description = "执行场景")
    private String fromScene;

    @Schema(description = "临时用户ID")
    private String endUser;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}