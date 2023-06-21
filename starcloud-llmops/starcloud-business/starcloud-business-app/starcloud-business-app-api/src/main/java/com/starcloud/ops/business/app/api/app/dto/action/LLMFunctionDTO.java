package com.starcloud.ops.business.app.api.app.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * LLM action 函数 DTO
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
@Schema(description = "LLM action 函数 DTO")
public class LLMFunctionDTO extends ActionDTO {

    private static final long serialVersionUID = 8566048247596256155L;

    /**
     * 模版变量
     */
    @Schema(description = "应用变量")
    private VariableDTO variable;

}
