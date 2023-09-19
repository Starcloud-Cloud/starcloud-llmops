package com.starcloud.ops.business.log.api.message.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * @author nacoyer
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "管理后台 - 应用执行日志结果 Excel 导出 Request VO，参数和 LogAppMessagePageReqVO 是一致的")
public class LogAppMessageExportReqVO implements Serializable {

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
     * ai model
     */
    @Schema(description = "ai model")
    private String aiModel;

    /**
     * app 配置
     */
    @Schema(description = "app 配置")
    private String appConfig;

    /**
     * 执行的 app step
     */
    @Schema(description = "执行的 app step")
    private String appStep;

    /**
     * 执行状态
     */
    @Schema(description = "执行状态，error：失败，success：成功")
    private String status;

    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private String errorCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * app 配置
     */
    @Schema(description = "app 配置")
    private String variables;

    /**
     * 请求内容
     */
    @Schema(description = "请求内容")
    private String message;

    /**
     * 消耗token数
     */
    @Schema(description = "消耗token数")
    private Integer messageTokens;

    /**
     * 消耗token单位价格
     */
    @Schema(description = "消耗token单位价格")
    private BigDecimal messageUnitPrice;

    /**
     * 返回内容
     */
    @Schema(description = "返回内容")
    private String answer;

    /**
     * 消耗token数
     */
    @Schema(description = "消耗token数")
    private Integer answerTokens;

    /**
     * 消耗token单位价格
     */
    @Schema(description = "消耗token单位价格")
    private BigDecimal answerUnitPrice;

    /**
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    private Long elapsed;

    /**
     * 总消耗价格
     */
    @Schema(description = "总消耗价格")
    private BigDecimal totalPrice;

    /**
     * 价格单位
     */
    @Schema(description = "价格单位")
    private String currency;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 执行者
     */
    @Schema(description = "临时用户ID")
    private String endUser;

    /**
     * 模版创建时间
     */
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}