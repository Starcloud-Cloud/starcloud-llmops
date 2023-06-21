package com.starcloud.ops.business.app.api.app.vo.request.variable;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * 应用请求 action 请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用变量请求对象")
public class VariableReqVO implements Serializable {

    private static final long serialVersionUID = 5828476130360610676L;

    /**
     * 应用变量
     */
    @Valid
    private List<VariableItemReqVO> variables;
}
