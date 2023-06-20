package com.starcloud.ops.business.app.api.app.vo.response.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * action 函数实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 函数响应对象 VO")
public class LLMFunctionRespVO extends ActionRespVO {

    private static final long serialVersionUID = -5693842217036667952L;

    /**
     * 模版变量
     */
    @Schema(description = "应用变量")
    private VariableRespVO variable;

}
