package com.starcloud.ops.business.log.api.conversation.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-30
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "LogAppConversationInfoRespVO", description = "应用会话信息响应 VO")
public class AppLogConversationInfoRespVO implements Serializable {

    private static final long serialVersionUID = 7875467036684665393L;

    /**
     * 会话 uid
     */
    @Schema(description = "会话uid")
    private String uid;

    /**
     * 应用 uid
     */
    @Schema(description = "应用 uid")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String appName;

    /**
     * 应用模式
     */
    @Schema(description = "应用模式")
    private String appMode;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 使用的 ai 模型
     */
    @Schema(description = "使用的 ai 模型")
    private String aiModel;

    /**
     * 请求总消耗token数
     */
    @Schema(description = "请求总消耗token数")
    private Integer totalMessageTokens;

    /**
     * 返回总消耗token数
     */
    @Schema(description = "返回总消耗token数")
    private Integer totalAnswerTokens;

    /**
     * token总数
     */
    @Schema(description = "token总数")
    private Integer tokens;

    /**
     * 消息总数
     */
    @Schema(description = "消息总数")
    private Integer messageCount;

    /**
     * 反馈总数
     */
    @Schema(description = "反馈总数")
    private Integer feedbacksCount;

    /**
     * 执行总耗时
     */
    @Schema(description = "执行总耗时")
    private BigDecimal totalElapsed;

    /**
     * 执行总花费
     */
    @Schema(description = "执行总花费")
    private BigDecimal totalPrice;

    /**
     * 消耗积分
     */
    @Schema(description = "消耗积分")
    private Integer costPoints;

    @Schema(description = "消耗图片点数")
    private Integer imagePoints;

    /**
     * 消耗矩阵点数
     */
    private Integer matrixPoints;

    /**
     * 执行状态，ERROR：失败，SUCCESS：成功
     */
    @Schema(description = "执行状态，ERROR：失败，SUCCESS：成功")
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
     * 会话创建人
     */
    @Schema(description = "注册用户ID")
    private String creator;

    /**
     * 游客唯一标识
     */
    @Schema(description = "注册用户ID")
    private String endUser;

    /**
     * 应用执行者（游客，用户，或者具体的用户）
     */
    @Schema(description = "应用执行者（游客，用户，或者具体的用户）")
    private String appExecutor;

    /**
     * 用户等级
     */
    @Schema(description = "用户等级")
    private List<String> userLevels;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}