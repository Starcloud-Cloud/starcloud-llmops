package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * AIGC 应用配置DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用配置DTO")
public class AppConfigDTO implements Serializable {

    private static final long serialVersionUID = 1575558145567574534L;

    /**
     * 应用步骤
     */
    @Schema(description = "应用步骤")
    private List<StepWrapperDTO> steps;

    /**
     * 应用变量
     */
    @Schema(description = "应用变量")
    private List<VariableDTO> variables;

}
