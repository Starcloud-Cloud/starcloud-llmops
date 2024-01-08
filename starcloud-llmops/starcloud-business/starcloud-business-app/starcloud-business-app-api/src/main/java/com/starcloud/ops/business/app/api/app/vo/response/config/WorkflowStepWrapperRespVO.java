package com.starcloud.ops.business.app.api.app.vo.response.config;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * App 步骤实体包装类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 工作流步骤包装请求对象")
public class WorkflowStepWrapperRespVO implements Serializable {

    private static final long serialVersionUID = -5726185087363176515L;

    /**
     * 步骤 field
     */
    @Schema(description = "步骤 field")
    private String field;

    /**
     * 步骤label
     */
    @Schema(description = "步骤label")
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
    private WorkflowStepRespVO flowStep;

    /**
     * 步骤变量
     */
    @Schema(description = "步骤变量")
    private VariableRespVO variable;


    public void putVariable(Map<String, Object> variable) {
        this.variable.putVariable(variable);
    }

    public Map<String, Object> getVariableItemMap() {
        return Optional.ofNullable(this.getVariable())
                .map(VariableRespVO::getVariables)
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_CONFIG_REQUIRED))
                .stream()
                .collect(Collectors.toMap(VariableItemRespVO::getField, VariableItemRespVO::getValue));
    }
}
