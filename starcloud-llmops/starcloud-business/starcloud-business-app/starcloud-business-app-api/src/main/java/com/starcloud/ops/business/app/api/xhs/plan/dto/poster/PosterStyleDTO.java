package com.starcloud.ops.business.app.api.xhs.plan.dto.poster;

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
import javax.validation.constraints.NotNull;
import java.util.Collections;
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
@Schema(description = "创作中心图片风格对象")
public class PosterStyleDTO implements java.io.Serializable {

    private static final long serialVersionUID = 3693634357817132472L;

    /**
     * 风格id
     */
    @Schema(description = "风格UUID")
    @NotBlank(message = "风格UUID不能为空！")
    private String uuid;

    /**
     * 风格序号
     */
    @Schema(description = "风格序号")
    private Integer index;

    /**
     * 风格名称
     */
    @Schema(description = "风格名称")
    @NotBlank(message = "风格名称不能为空！")
    private String name;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    @NotNull(message = "是否启用不能为空！")
    private Boolean enable;

    /**
     * 是否是系统风格
     */
    @Schema(description = "是否是系统风格")
    private Boolean system;

    /**
     * 该风格下的图片类型变量总数量
     */
    @Schema(description = "该风格下的图片类型变量总数量")
    private Integer totalImageCount;

    /**
     * 海报风格描述
     */
    @Schema(description = "海报风格描述")
    private String description;

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
        AppValidate.notBlank(uuid, "{}, 风格UUID不能为空！请联系管理员！", this.name);
        AppValidate.notEmpty(templateList, "{}, 请选择图片模板！", this.name);
        templateList.forEach(PosterTemplateDTO::validate);
    }

    /**
     * 固定风格1
     *
     * @return 风格1
     */
    public static PosterStyleDTO ofOne() {
        PosterStyleDTO posterStyle = new PosterStyleDTO();
        posterStyle.setName("风格 1");
        posterStyle.setEnable(Boolean.TRUE);
        posterStyle.setSystem(Boolean.TRUE);
        posterStyle.setTemplateList(Collections.singletonList(PosterTemplateDTO.ofMain()));
        return posterStyle;
    }

}