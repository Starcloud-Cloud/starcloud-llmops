package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeReferenceDTO;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class ParagraphSchemeStepDTO extends BaseSchemeStepDTO {

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
    private List<CreativeSchemeReferenceDTO> refers;

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
    private List<VariableItemDTO> variables;

    /**
     * 转换到应用参数
     */
    @Override
    public void convertAppStepWrapper(WorkflowStepWrapperRespVO stepWrapper) {

        //处理随机，让应用step 执行时不需要处理过多的业务逻辑

        //处理 从参考段落（很多）随机取出 N条

        //

    }

    /**
     * 转换到创作方案参数
     */
    @Override
    public void convertCreativeSchemeStep() {

    }

    /**
     * 转换为 map
     *
     * @return map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(CreativeConstants.GENERATE_MODEL, model);
        map.put(CreativeConstants.REFERS, refers);
        map.put(CreativeConstants.PARAGRAPH_COUNT, paragraphCount);
        map.put(CreativeConstants.REQUIREMENT, requirement);
        return map;
    }


}
