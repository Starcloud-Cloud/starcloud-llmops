package com.starcloud.ops.business.app.service.limit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.limit.dto.LimitConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-29
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppLimitContext", description = "限流执行请求上下文")
public class AppLimitContext implements Serializable {

    private static final long serialVersionUID = -3754250618442134116L;

    /**
     * 应用唯一标识
     */
    @Schema(description = "应用唯一标识")
    private String appUid;

    /**
     * 日志消息创建者
     */
    @Schema(description = "日志消息创建者")
    private String userId;

    /**
     * 游客唯一表示
     */
    @Schema(description = "游客唯一表示")
    private String endUser;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * Redis key
     */
    @Schema(description = "Redis key")
    private String limitKey;

    /**
     * 限流配置
     */
    @Schema(description = "限流配置")
    private LimitConfigDTO config;

}
