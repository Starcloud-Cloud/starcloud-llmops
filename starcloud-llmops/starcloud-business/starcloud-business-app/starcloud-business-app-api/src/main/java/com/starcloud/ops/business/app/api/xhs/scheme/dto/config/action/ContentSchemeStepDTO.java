package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.reference.ReferenceSchemeDTO;
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
public class ContentSchemeStepDTO extends BaseSchemeStepDTO {

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
    private List<ReferenceSchemeDTO> referList;

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

}
