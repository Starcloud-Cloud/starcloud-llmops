package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class StandardSchemeStepEntity extends BaseSchemeStepEntity {

    private static final long serialVersionUID = 2298470913179114149L;

    /**
     * 创作方案生成模式
     */
    @Schema(description = "创作方案生成模式")
    private String model;

//    /**
//     * 创作方案参考内容
//     */
//    @Schema(description = "创作方案参考内容")
//    private List<ReferenceSchemeEntity> referList;

    /**
     * 参考内容 素材库格式
     */
    @Schema(description = "参考内容-素材库格式")
    private List<AbstractBaseCreativeMaterialDTO> materialList;

    @Schema(description = "素材类型")
    private String materialType;

    /**
     * 创作方案步骤要求
     */
    @Schema(description = "创作方案步骤要求")
    private String requirement;

    /**
     * 创作方案步骤变量
     */
    @Schema(description = "创作方案步骤变量")
    private List<VariableItemDTO> variableList;

    /**
     * 组装为应用步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformAppStep(WorkflowStepWrapperRespVO stepWrapper) {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.REFERS, JsonUtils.toJsonString(this.materialList));
        variableMap.put(CreativeConstants.MATERIAL_TYPE, materialType);
        variableMap.put(CreativeConstants.GENERATE_MODE, this.model);
        variableMap.put(CreativeConstants.REQUIREMENT, CreativeAppUtils.handlerRequirement(this.requirement, this.variableList));
        stepWrapper.putVariable(variableMap);
    }

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformSchemeStep(WorkflowStepWrapperRespVO stepWrapper) {
        VariableRespVO variable = stepWrapper.getVariable();
        List<VariableItemRespVO> variables = variable.getVariables();

        for (VariableItemRespVO variableItem : variables) {
            if (CreativeConstants.GENERATE_MODE.equals(variableItem.getField())) {
                this.model = String.valueOf(Optional.ofNullable(variableItem.getValue()).orElse(CreativeSchemeGenerateModeEnum.AI_PARODY.name()));
            } else if (CreativeConstants.REFERS.equals(variableItem.getField())) {
                String refers = String.valueOf(Optional.ofNullable(variableItem.getValue()).orElse("[]"));
                refers = StringUtils.isBlank(refers) ? "[]" : refers;
                this.materialList = JsonUtils.parseArray(refers, AbstractBaseCreativeMaterialDTO.class);
            } else if (CreativeConstants.REQUIREMENT.equals(variableItem.getField())) {
                this.requirement = String.valueOf(Optional.ofNullable(variableItem.getValue()).orElse(StringUtils.EMPTY));
            } else if (CreativeConstants.MATERIAL_TYPE.equals(variableItem.getField())) {
                this.materialType = String.valueOf(Optional.ofNullable(variableItem.getValue()).orElse(StringUtils.EMPTY));
            }
        }
    }

}
