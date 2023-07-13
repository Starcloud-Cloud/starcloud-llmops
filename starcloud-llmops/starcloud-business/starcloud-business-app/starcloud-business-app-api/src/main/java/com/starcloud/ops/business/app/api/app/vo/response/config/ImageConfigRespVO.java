package com.starcloud.ops.business.app.api.app.vo.response.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;

/**
 * 图片生成配置
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
@Schema(description = "图片生成配置")
public class ImageConfigRespVO extends BaseConfigRespVO {

    private static final long serialVersionUID = -4540655599582546170L;

    /**
     * 模版变量
     */
    @Schema(description = "模版变量")
    private VariableRespVO variable;

}
