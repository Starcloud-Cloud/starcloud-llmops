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
public class ParagraphSchemeStepEntity extends StandardSchemeStepEntity {

    private static final long serialVersionUID = 6843541753056072604L;

    /**
     * 创作方案步骤生成的段落数
     */
    @Schema(description = "创作方案步骤生成的段落数")
    private Integer paragraphCount;

    /**
     * 组装为应用步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformAppStep(WorkflowStepWrapperRespVO stepWrapper) {
        super.doTransformAppStep(stepWrapper);
        Map<String, Object> params = new HashMap<>();
        params.put(CreativeConstants.PARAGRAPH_COUNT, this.paragraphCount);
        stepWrapper.putVariable(params);
    }

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    @Override
    protected void doTransformSchemeStep(WorkflowStepWrapperRespVO stepWrapper) {
        super.doTransformSchemeStep(stepWrapper);
        VariableItemRespVO variable = stepWrapper.getVariable(CreativeConstants.PARAGRAPH_COUNT);
        if (variable == null) {
            this.paragraphCount = 4;

        } else if (variable.getValue() == null) {
            this.paragraphCount = 4;
        } else {
            int count = 4;
            try {
                count = Integer.parseInt(String.valueOf(variable.getValue()));
            } catch (Exception e) {
                // ignore
            }

            this.paragraphCount = count;
        }

    }
}
