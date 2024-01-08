package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.reference.ReferenceSchemeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
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
public class ContentSchemeStepEntity extends BaseSchemeStepEntity {

    private static final long serialVersionUID = -1503267053868950469L;

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
     * 转换到应用参数
     */
    @Override
    public void convertAppStepWrapper(WorkflowStepWrapperRespVO stepWrapper) {

    }

    /**
     * 转换到创作方案参数
     */
    @Override
    public void convertCreativeSchemeStep() {
        this.model = CreativeSchemeGenerateModeEnum.RANDOM.name();
        this.referList = Collections.emptyList();
        this.requirement = "";
        this.variableList = Collections.emptyList();
    }


}
