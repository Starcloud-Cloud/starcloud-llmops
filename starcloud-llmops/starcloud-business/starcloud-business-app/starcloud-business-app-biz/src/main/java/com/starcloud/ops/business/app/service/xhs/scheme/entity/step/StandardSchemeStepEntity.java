package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.reference.ReferenceSchemeEntity;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
public abstract class StandardSchemeStepEntity extends BaseSchemeStepEntity {

    private static final long serialVersionUID = 2298470913179114149L;

    /**
     * 创作方案生成模式
     */
    @Schema(description = "创作方案生成模式")
    private String model;

    /**
     * 创作方案参考内容
     */
    @Schema(description = "创作方案参考内容")
    private List<ReferenceSchemeEntity> referList;

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
        List<ReferenceSchemeEntity> referenceSchemeList = CreativeAppUtils.handlerReferencesEntity(this.referList);
        if (CreativeSchemeGenerateModeEnum.AI_PARODY.name().equals(this.model)) {
            List<ReferenceSchemeEntity> referList = new ArrayList<>();
            if (referenceSchemeList.size() <= 1) {
                referList = referenceSchemeList;
            } else {
                for (int i = 0; i < 1; i++) {
                    referList.add(referenceSchemeList.get(RandomUtil.randomInt(referenceSchemeList.size())));
                }
            }
            variableMap.put(CreativeConstants.REFERS, JSONUtil.toJsonStr(referList));
        } else {
            variableMap.put(CreativeConstants.REFERS, JSONUtil.toJsonStr(referenceSchemeList));
        }
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
        this.model = CreativeSchemeGenerateModeEnum.AI_PARODY.name();
        this.referList = Collections.emptyList();
        this.requirement = "";
        this.variableList = Collections.emptyList();
    }

}
