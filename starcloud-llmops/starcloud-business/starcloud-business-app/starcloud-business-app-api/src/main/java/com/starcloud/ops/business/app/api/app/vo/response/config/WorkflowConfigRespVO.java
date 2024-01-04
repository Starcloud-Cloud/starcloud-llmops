package com.starcloud.ops.business.app.api.app.vo.response.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
public class WorkflowConfigRespVO extends BaseConfigRespVO {

    private static final long serialVersionUID = -4540655599582546170L;

    /**
     * 模版步骤
     */
    @Schema(description = "模版步骤")
    private List<WorkflowStepWrapperRespVO> steps;

    /**
     * 模版变量
     */
    @Schema(description = "模版变量")
    private VariableRespVO variable;

    public void putVariable(String stepId, Map<String, Object> variable) {
        for (WorkflowStepWrapperRespVO step : steps) {
            if (stepId.equals(step.getField())) {
                step.putVariable(variable);
                break;
            }
        }
    }
}
