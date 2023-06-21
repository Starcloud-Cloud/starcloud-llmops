package com.starcloud.ops.business.app.api.app.vo.request.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用工作流函数请求对象")
public class WorkflowConfigReqVO extends BaseConfigReqVO {

    private static final long serialVersionUID = -5607691388056493709L;

    /**
     * 模版步骤
     */
    @Schema(description = "模版步骤")
    @Valid
    private List<WorkflowStepWrapperReqVO> steps;

    /**
     * 模版变量
     */
    @Schema(description = "模版变量")
    @Valid
    private VariableReqVO variable;

}
