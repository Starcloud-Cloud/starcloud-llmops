package com.starcloud.ops.business.app.api.app.vo.response.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * 应用请求 action 请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用请求 action 请求对象")
public class ActionRespVO implements Serializable {

    private static final long serialVersionUID = 5848613340472242430L;

    /**
     * 动作名称
     */
    @Schema(description = "动作(step)名称")
    private String name;

    /**
     * 步骤类型
     */
    @Schema(description = "步骤类型")
    private String type;

    /**
     * 动作处理器
     */
    @Schema(description = "动作(step)处理器")
    private String handler;

    /**
     * 动作执行结果
     */
    @Schema(description = "动作(step)执行结果")
    private ActionResponseRespVO response;

    /**
     * 动作描述
     */
    @Schema(description = "动作(step)描述")
    private String description;

}
