package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.convert.xhs.scheme.CreativeSchemeStepConvert;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VariableSchemeStepEntity extends BaseSchemeStepEntity {

    private static final long serialVersionUID = 9170891502442764719L;

    /**
     * 应用变量
     */
    @Schema(description = "应用变量")
    private List<VariableItemEntity> variableList;

    /**
     * 组装为应用步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformAppStep(WorkflowStepWrapperRespVO stepWrapper) {
        List<VariableItemRespVO> variables = CreativeSchemeStepConvert.INSTANCE.convertToResponse(this.variableList);
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(variables);
        stepWrapper.setVariable(variable);
    }

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformSchemeStep(WorkflowStepWrapperRespVO stepWrapper) {
        VariableRespVO variable = stepWrapper.getVariable();
        this.variableList = CreativeSchemeStepConvert.INSTANCE.convertToEntity(CollectionUtil.emptyIfNull(variable.getVariables()));
    }
}