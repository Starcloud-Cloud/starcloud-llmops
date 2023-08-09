package com.starcloud.ops.business.app.api.log.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图片生成应用日志实体")
public class AppLogMessageRespVO implements Serializable {

    private static final long serialVersionUID = 2066994526095906744L;

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
     * 消息内容
     */
    @Schema(description = "消耗 token 数量")
    private Integer tokens;

    /**
     * 消息内容
     */
    @Schema(description = "价格")
    private BigDecimal price;

    /**
     * 消息内容
     */
    @Schema(description = "价格单位")
    private String currency;

    /**
     * 消息内容
     */
    @Schema(description = "用户")
    private String user;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 应用信息
     */
    @Schema(description = "应用信息")
    private AppRespVO app;

}
