package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
     * 转换到应用参数
     *
     * @param stepWrapper
     */
    @Override
    public void convertAppStepWrapper(WorkflowStepWrapperRespVO stepWrapper) {

    }

    /**
     * 转换到创作方案参数
     */
    @Override
    public void convertCreativeSchemeStep() {
        this.requirement = "";
    }
}
