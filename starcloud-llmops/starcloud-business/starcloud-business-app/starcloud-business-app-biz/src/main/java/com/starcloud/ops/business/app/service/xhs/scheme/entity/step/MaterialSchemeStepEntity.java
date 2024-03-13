package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MaterialSchemeStepEntity extends BaseSchemeStepEntity {

    private static final long serialVersionUID = -1431157620004839828L;

    /**
     * 创作方案资料库类型
     */
    @Schema(description = "创作方案资料库类型")
    private String materialType;

    /**
     * 组装为应用步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformAppStep(WorkflowStepWrapperRespVO stepWrapper) {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.MATERIAL_TYPE, this.materialType);
        stepWrapper.putVariable(variableMap);
    }

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformSchemeStep(WorkflowStepWrapperRespVO stepWrapper) {
        VariableItemRespVO materialTypeVariable = stepWrapper.getVariable(CreativeConstants.MATERIAL_TYPE);
        if (materialTypeVariable != null) {
            this.materialType = String.valueOf(materialTypeVariable.getValue());
        }
    }
}
