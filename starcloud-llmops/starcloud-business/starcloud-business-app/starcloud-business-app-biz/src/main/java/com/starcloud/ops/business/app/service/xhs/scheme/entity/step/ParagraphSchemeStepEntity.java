package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.reference.ReferenceSchemeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class ParagraphSchemeStepEntity extends BaseSchemeStepEntity {

    private static final long serialVersionUID = 6843541753056072604L;

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
     * 创作方案步骤生成的段落数
     */
    @Schema(description = "创作方案步骤生成的段落数")
    private Integer paragraphCount;

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
        variableMap.put(CreativeConstants.REFERS, this.referList);
        variableMap.put(CreativeConstants.GENERATE_MODE, this.model);
        variableMap.put(CreativeConstants.PARAGRAPH_COUNT, this.paragraphCount);
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
        this.model = CreativeSchemeGenerateModeEnum.AI_PARODY.name();
        this.referList = Collections.emptyList();
        this.paragraphCount = 4;
        this.requirement = "";
        this.variableList = Collections.emptyList();
    }
}