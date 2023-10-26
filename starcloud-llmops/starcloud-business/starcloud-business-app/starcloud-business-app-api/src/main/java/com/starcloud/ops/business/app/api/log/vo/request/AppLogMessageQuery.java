package com.starcloud.ops.business.app.api.log.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.temporal.ChronoUnit;

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
@Schema(name = "AppLogMessageQuery", description = "图片生成应用日志实体")
public class AppLogMessageQuery extends PageQuery {

    private static final long serialVersionUID = 7099511822888585890L;

    /**
     * 应用唯一标识
     */
    @Schema(description = "应用唯一标识")
    @NotBlank(message = "应用唯一标识不能为空")
    private String appUid;

    /**
     * 应用模式
     */
    @Schema(description = "应用模式")
    private String appMode;

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
     * 时间
     */
    @Schema(description = "查询时间段")
    private Long timeInterval;

    /**
     * 应用市场唯一标识
     */
    @Schema(description = "时间单位")
    private ChronoUnit timeUnit;


}
