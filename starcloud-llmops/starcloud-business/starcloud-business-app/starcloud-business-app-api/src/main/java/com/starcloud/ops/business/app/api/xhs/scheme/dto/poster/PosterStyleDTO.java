package com.starcloud.ops.business.app.api.xhs.scheme.dto.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.AppValidate;
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
 * @since 2023-11-07
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "PosterStyleDTO", description = "创作中心图片风格对象")
public class PosterStyleDTO implements java.io.Serializable {

    private static final long serialVersionUID = 3693634357817132472L;

    /**
     * 风格id
     */
    @Schema(description = "风格ID")
    @NotBlank(message = "风格ID不能为空！")
    private String id;

    /**
     * 风格名称
     */
    @Schema(description = "风格名称")
    @NotBlank(message = "风格名称不能为空！")
    private String name;

    /**
     * 该风格下的图片类型变量总数量
     */
    @Schema(description = "该风格下的图片类型变量总数量")
    private Integer totalImageCount;

    /**
     * 一组风格下的图片类型变量最大数量
     */
    @Schema(description = "一组风格下的图片类型变量最大数量")
    private Integer maxTotalImageCount;

    /**
     * 图片素材列表
     */
    @Schema(description = "图片素材列表")
    private List<String> imageMaterialList;

    /**
     * 模板列表
     */
    @Schema(description = "模板列表")
    @Valid
    @NotEmpty(message = "请选择图片模板！")
    private List<PosterTemplateDTO> templateList;

    /**
     * 校验风格对象
     */
    public void validate() {
        AppValidate.notBlank(id, "{}, 风格ID不能为空！请联系管理员！", this.name);
        AppValidate.notEmpty(templateList, "{}, 请选择图片模板！", this.name);
        templateList.forEach(PosterTemplateDTO::validate);
    }

}
