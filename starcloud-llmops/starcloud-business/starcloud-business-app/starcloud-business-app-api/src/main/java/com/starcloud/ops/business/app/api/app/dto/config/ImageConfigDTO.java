package com.starcloud.ops.business.app.api.app.dto.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 生成图片应用配置DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "生成图片应用配置DTO")
public class ImageConfigDTO extends BaseConfigDTO {

    private static final long serialVersionUID = -6389286712628894407L;

    /**
     * 模版变量
     */
    @Schema(description = "模版变量")
    private VariableDTO variable;

}
