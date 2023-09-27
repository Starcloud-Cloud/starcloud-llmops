package com.starcloud.ops.business.log.api.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 应用执行日志结果 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 *
 * @author nacoyer
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "LogAppMessageBaseVO", description = "应用执行日志消息 Base VO")
public class LogAppMessageBaseVO implements Serializable {

    private static final long serialVersionUID = -1821463952351686261L;

    /**
     * 消息UID
     */
    @Schema(description = "消息uid")
    @NotNull(message = "消息uid不能为空")
    private String uid;

    /**
     * 会话UID
     */
    @Schema(description = "会话UID")
    @NotNull(message = "会话UID不能为空")
    private String appConversationUid;

    /**
     * app uid
     */
    @Schema(description = "app uid")
    @NotNull(message = "app uid不能为空")
    private String appUid;

    /**
     * app name
     */
    @Schema(description = "app 模式")
    @NotNull(message = "app 模式不能为空")
    private String appMode;

    /**
     * app 配置
     */
    @Schema(description = "app 配置")
    private String appConfig = "{}";

    /**
     * 执行的 app step
     */
    @Schema(description = "执行的 app step")
    @NotNull(message = "执行的 app step不能为空")
    private String appStep;

    /**
     * 执行状态，error：失败，success：成功
     */
    @Schema(description = "执行状态，error：失败，success：成功")
    @NotNull(message = "执行状态，error：失败，success：成功不能为空")
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
    @NotNull(message = "app 配置不能为空")
    private String variables = "{}";

    /**
     * 请求内容
     */
    @Schema(description = "请求内容")
    @NotNull(message = "请求内容不能为空")
    private String message = "";

    /**
     * 消耗token数
     */
    @Schema(description = "消耗token数")
    @NotNull(message = "Message 消耗token数不能为空")
    private Integer messageTokens = 0;

    /**
     * 消耗token单位价格
     */
    @Schema(description = "消耗token单位价格")
    @NotNull(message = "Message 消耗token单位价格不能为空")
    private BigDecimal messageUnitPrice = BigDecimal.ZERO;

    /**
     * 返回内容
     */
    @Schema(description = "返回内容")
    private String answer = "";

    /**
     * 消耗token数
     */
    @Schema(description = "消耗token数")
    @NotNull(message = "Answer 消耗token数不能为空")
    private Integer answerTokens = 0;

    /**
     * 消耗token单位价格
     */
    @Schema(description = "消耗token单位价格")
    @NotNull(message = "Answer 消耗token单位价格不能为空")
    private BigDecimal answerUnitPrice = BigDecimal.ZERO;

    /**
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    @NotNull(message = "执行耗时不能为空")
    private Long elapsed;

    /**
     * 总消耗价格
     */
    @Schema(description = "总消耗价格")
    @NotNull(message = "总消耗价格不能为空")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 价格单位
     */
    @Schema(description = "价格单位")
    @NotNull(message = "价格单位不能为空")
    private String currency;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    @NotNull(message = "执行场景不能为空")
    private String fromScene;

    /**
     * 终端用户ID
     */
    @Schema(description = "临时用户ID")
    private String endUser;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String msgType;

    /**
     * 消息子类型
     */
    @Schema(description = "渠道uid")
    private String mediumUid;

    /**
     * AI 模型
     */
    @Schema(description = "AI 模型")
    private String aiModel;

}