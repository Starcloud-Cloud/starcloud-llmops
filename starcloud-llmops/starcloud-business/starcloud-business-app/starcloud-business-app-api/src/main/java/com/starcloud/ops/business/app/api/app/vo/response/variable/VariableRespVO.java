package com.starcloud.ops.business.app.api.app.vo.response.variable;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 应用请求 action 请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用变量响应对象VO")
public class VariableRespVO implements Serializable {

    private static final long serialVersionUID = 8559448054450544431L;

    /**
     * 应用变量
     */
    @Schema(description = "应用变量")
    private List<VariableItemRespVO> variables;
}
