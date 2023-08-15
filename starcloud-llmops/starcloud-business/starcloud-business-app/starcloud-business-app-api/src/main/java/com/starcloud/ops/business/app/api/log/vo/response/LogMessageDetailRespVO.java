package com.starcloud.ops.business.app.api.log.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用日志详情基础实体")
public class LogMessageDetailRespVO implements Serializable {

    private static final long serialVersionUID = -1475563091120656009L;

    /**
     * 消息唯一标识
     */
    @Schema(description = "消息唯一标识")
    private String uid;

    /**
     * 消息会话唯一标识
     */
    @Schema(description = "消息会话唯一标识")
    private String conversationUid;

    /**
     * 消息会话唯一标识
     */
    @Schema(description = "App 唯一标识")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "App 名称")
    private String appName;

    /**
     * 消息类型
     */
    @Schema(description = "消息模型")
    private String appMode;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String message;

    /**
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    private Long elapsed;

    /**
     * 成功标志
     */
    @Schema(description = "成功标志")
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
    private String errorMessage;

    /**
     * 应用执行者（游客，用户，或者具体的用户）
     */
    private String appExecutor;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 图片
     */
    @Schema(description = "images")
    private List<String> images;

    /**
     * 返回内容
     */
    @Schema(description = "返回内容")
    private String answer;

}
