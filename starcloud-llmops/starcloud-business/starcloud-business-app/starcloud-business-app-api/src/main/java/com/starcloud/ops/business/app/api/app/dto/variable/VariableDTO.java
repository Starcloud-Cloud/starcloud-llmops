package com.starcloud.ops.business.app.api.app.dto.variable;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 应用变量对象DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用变量对象DTO")
public class VariableDTO implements Serializable {

    private static final long serialVersionUID = 8322223895221116150L;

    /**
     * 应用变量
     */
    @Schema(description = "应用变量")
    private List<VariableItemDTO> variables;
}
