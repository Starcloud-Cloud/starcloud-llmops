package com.starcloud.ops.business.app.api.scheme.dto;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativeSchemeCopyWritingTemplateDTO", description = "文案生成模板")
public class CreativeSchemeCopyWritingTemplateDTO implements java.io.Serializable {

    private static final long serialVersionUID = 2702779004299599670L;

    /**
     * 是否推广到微信公共号
     */
    @Schema(description = "是否推广到微信公共号")
    private Boolean isPromoteMp;

    /**
     * 微信公共号
     */
    @Schema(description = "微信公共号")
    private String mpCode;

    /**
     * 生成文案的要求
     */
    @Schema(description = "生成文案要求")
    @NotBlank(message = "生成文案要求不能为空")
    private String demand;

    /**
     * 文案生成模板变量
     */
    @Schema(description = "文案生成模板变量")
    private List<VariableItemDTO> variables;

    /**
     * 文案生成模板示例
     */
    @Schema(description = "文案生成模板示例")
    private List<CopyWritingExample> example;
}
