package com.starcloud.ops.business.app.api.xhs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "XhsImageTemplateDTO", description = "小红书图片模板")
public class XhsImageTemplateDTO implements java.io.Serializable {

    private static final long serialVersionUID = 20366484377479364L;

    /**
     * 图片模板ID
     */
    @Schema(description = "图片模板ID")
    @NotBlank(message = "图片模板ID不能为空！")
    private String id;

    /**
     * 图片海报ID
     */
    @Schema(description = "海报ID")
    private String posterId;

    /**
     * 图片模板名称
     */
    @Schema(description = "图片模板名称")
    @NotBlank(message = "图片模板名称不能为空!")
    private String name;

    /**
     * 应用token
     */
    @Schema(description = "应用token")
    private String token;

    /**
     * 图片数量
     */
    @Schema(description = "图片数量")
    private Integer imageNumber;

    /**
     * 示例图片
     */
    @Schema(description = "示例图片")
    private String example;

    /**
     * 图片模板变量
     */
    @Schema(description = "图片模板变量")
    @NotEmpty(message = "图片模板变量不能为空！")
    private List<VariableItemDTO> variables;

}
