package com.starcloud.ops.business.app.api.xhs.scheme.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
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
 * @since 2023-11-02
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CreativeImageTemplateDTO", description = "创作中心图片模板对象")
public class CreativeImageTemplateDTO implements java.io.Serializable {

    private static final long serialVersionUID = 20366484377479364L;

    /**
     * 图片模板ID
     */
    @Schema(description = "图片模板ID")
    @NotBlank(message = "图片模板ID不能为空！")
    private String id;

    /**
     * 图片模板名称
     */
    @Schema(description = "图片模板名称")
    @NotBlank(message = "图片模板名称不能为空!")
    private String name;

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
    private List<VariableItemRespVO> variables;

}
