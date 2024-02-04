package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AssembleSchemeStepEntity extends BaseSchemeStepEntity {

    private static final long serialVersionUID = 4820280880902279978L;

    /**
     * 创作方案步骤要求
     */
    @Schema(description = "创作方案步骤要求")
    private String requirement;


    /**
     * 组装为应用步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformAppStep(WorkflowStepWrapperRespVO stepWrapper) {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.REQUIREMENT, this.requirement);
        stepWrapper.putVariable(variableMap);
    }

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformSchemeStep(WorkflowStepWrapperRespVO stepWrapper) {
        VariableItemRespVO variable = stepWrapper.getVariable(CreativeConstants.REQUIREMENT);
        if (variable != null) {
            this.requirement = String.valueOf(variable.getValue());
        }

    }
}
