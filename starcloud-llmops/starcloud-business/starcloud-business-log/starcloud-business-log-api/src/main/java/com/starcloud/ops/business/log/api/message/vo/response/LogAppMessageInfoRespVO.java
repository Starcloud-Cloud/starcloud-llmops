package com.starcloud.ops.business.log.api.message.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

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
@Schema(name = "LogAppMessageInfoRespVO", description = "管理后台 - 应用执行日志结果 Base VO")
public class LogAppMessageInfoRespVO implements Serializable {

    private static final long serialVersionUID = 6417965474946970538L;

    /**
     * 消息uid
     */
    @Schema(description = "消息uid")
    private String uid;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private String appConversationUid;

    /**
     * app uid
     */
    @Schema(description = "app uid")
    private String appUid;

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
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    private Long elapsed;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String user;

    /**
     * 临时用户ID
     */
    @Schema(description = "临时用户ID")
    private String endUser;

    /**
     * 模版创建时间
     */
    @Schema(description = "ai模型")
    private String aiModel;

}