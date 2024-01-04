package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeReferenceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * 创作方案 段落节点配置
 */
@Data
public class ParagraphSchemeStepDTO extends BaseSchemeStepDTO {



    /**
     * 创作方案步骤要求
     */
    @Schema(description = "创作方案步骤要求")
    private String requirement;

    @Schema(description = "创作方案参考内容")
    private List<CreativeSchemeReferenceDTO> refers;

    //生成模式
    private String type;

    @Schema(description = "创作方案步骤变量")
    private List<VariableItemDTO> variables;


    /**
     * 生成的段落数
     */
    private Integer size;

    /**
     * 转换到应用参数
     */
    @Override
    public void convertApp(WorkflowStepWrapperRespVO workflowStepWrapperRespVO) {

        //处理随机，让应用step 执行时不需要处理过多的业务逻辑

        //处理 从参考段落（很多）随机取出 N条

        //

        workflowStepWrapperRespVO.getVariable().getVariables().add(
                VariableItemRespVO.ofTextVariable(ParagraphActionHandler.DL, this.getRequirement())
        );

    }

    /**
     * 转换到创作方案参数
     */
    @Override
    public void convertCreative() {

    }


}
