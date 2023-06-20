package com.starcloud.ops.business.app.api.app.vo.request.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.enums.app.AppStepTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
@Schema(description = "应用请求 action 请求对象 VO")
public class ActionReqVO implements Serializable {

    private static final long serialVersionUID = -5742856481114008772L;

    /**
     * 动作名称
     */
    @Schema(description = "动作(step)名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "动作(step)名称不能为空")
    private String name;

    /**
     * 步骤类型
     */
    @Schema(description = "步骤类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "步骤类型不能为空")
    @InEnum(value = AppStepTypeEnum.class, message = "步骤类型[{value}]必须属于: {values}")
    private String type;

    /**
     * 动作处理器
     */
    @Schema(description = "动作(step)处理器", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "动作(step)处理器不能为空")
    private String handler;

    /**
     * 动作执行结果
     */
    @Schema(description = "动作(step)执行结果")
    @Valid
    private ActionResponseReqVO response;

    /**
     * 动作描述
     */
    @Schema(description = "动作(step)描述")
    private String description;
}
