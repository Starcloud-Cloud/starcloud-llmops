package com.starcloud.ops.business.log.api.message.vo;

import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
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
    @Schema(description = "日志应用消息UID")
    @NotBlank(message = "日志应用消息【uid】是必填项！")
    private String uid;

    /**
     * 会话UID
     */
    @Schema(description = "日志应用会话UID")
    @NotBlank(message = "日志应用会话【appConversationUid】是必填项！")
    private String appConversationUid;

    /**
     * 应用UID
     */
    @Schema(description = "应用UID")
    @NotBlank(message = "应用唯一标识【appUid】是必填项！")
    private String appUid;

    /**
     * 应用模式
     */
    @Schema(description = "应用模式")
    @NotBlank(message = "应用模式【appMode】是必填项！")
    private String appMode;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    @NotBlank(message = "执行场景【fromScene】是必填项！")
    private String fromScene;

    /**
     * AI 模型
     */
    @Schema(description = "AI 模型")
    private String aiModel;

    /**
     * 消息子类型
     */
    @Schema(description = "渠道uid")
    private String mediumUid;


    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String msgType;

    /**
     * 执行的应用步骤
     */
    @Schema(description = "执行的应用步骤")
    @NotBlank(message = "执行的应用步骤【appStep】是必填项！")
    private String appStep;

    /**
     * 应用配置
     */
    @Schema(description = "应用配置")
    @NotNull(message = "应用配置【appConfig】是必填项！")
    private String appConfig = "{}";

    /**
     * 应用请求变量
     */
    @Schema(description = "应用请求变量")
    @NotNull(message = "应用请求变量【variables】是必填项")
    private String variables = "{}";

    /**
     * 请求内容
     */
    @Schema(description = "请求内容")
    @NotNull(message = "请求内容【message】是必填项")
    private String message = "";

    /**
     * 请求消耗token数
     */
    @Schema(description = "请求消耗token数")
    @NotNull(message = "请求消耗token数【messageTokens】是必填项！")
    private Integer messageTokens = 0;

    /**
     * 请求消耗token单位价格
     */
    @Schema(description = "请求消耗token单位价格")
    @NotNull(message = "请求请求消耗token单位价格【messageUnitPrice】是必填项！")
    private BigDecimal messageUnitPrice = BigDecimal.ZERO;

    /**
     * 返回内容
     */
    @Schema(description = "返回内容")
    private String answer = "";

    /**
     * 响应消耗token数
     */
    @Schema(description = "响应消耗token数")
    @NotNull(message = "响应消耗token数【answerTokens】是必填项！")
    private Integer answerTokens = 0;

    /**
     * 响应消耗token单位价格
     */
    @Schema(description = "响应消耗token单位价格")
    @NotNull(message = "响应消耗token单位价格【answerUnitPrice】是必填项！")
    private BigDecimal answerUnitPrice = BigDecimal.ZERO;

    /**
     * 总消耗价格
     */
    @Schema(description = "总消耗价格")
    @NotNull(message = "总消耗价格【totalPrice】是必填项！")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 价格单位
     */
    @Schema(description = "价格单位")
    @NotNull(message = "价格单位【currency】不能为空")
    private String currency = "USD";

    /**
     * 消耗积分
     */
    @Schema(description = "消耗积分")
    @NotNull(message = "消耗积分【costPoints】是必填项！")
    private Integer costPoints = 0;

    /**
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    @NotNull(message = "执行耗时【elapsed】是必填项！")
    private Long elapsed;

    /**
     * 会话状态，error：失败，success：成功
     */
    @Schema(description = "会话状态，ERROR：失败，SUCCESS：成功")
    @InEnum(value = LogStatusEnum.class, field = InEnum.EnumField.NAME, message = "会话状态[{value}], 不在合法范围内, 有效值：{values}")
    @NotBlank(message = "会话状态【status】是必填项！")
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
     * 终端用户ID
     */
    @Schema(description = "临时用户ID")
    private String endUser;


}