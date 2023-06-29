package com.starcloud.ops.business.app.api.app.vo.request.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.action.WorkflowStepReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * App 步骤实体包装类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 工作流步骤包装请求对象")
public class WorkflowStepWrapperReqVO implements Serializable {

    private static final long serialVersionUID = 2149218794764354158L;

    /**
     * 步骤 field
     */
    @Schema(description = "步骤 field")
    @NotBlank(message = "步骤 field 不能为空")
    private String field;

    /**
     * 步骤label
     */
    @Schema(description = "步骤label")
    @NotEmpty(message = "步骤名称不能为空")
    private String name;

    /**
     * 步骤按钮label
     */
    @Schema(description = "步骤按钮label")
    private String buttonLabel;

    /**
     * 步骤描述
     */
    @Schema(description = "步骤描述")
    private String description;

    /**
     * 具体的步骤配置
     */
    @Schema(description = "具体的步骤配置")
    @Valid
    private WorkflowStepReqVO flowStep;

    /**
     * 步骤变量
     */
    @Schema(description = "步骤变量")
    @Valid
    private VariableReqVO variable;


}
