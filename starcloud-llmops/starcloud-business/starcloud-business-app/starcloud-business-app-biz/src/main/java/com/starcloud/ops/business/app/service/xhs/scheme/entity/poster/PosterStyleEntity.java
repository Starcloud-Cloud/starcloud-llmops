package com.starcloud.ops.business.app.service.xhs.scheme.entity.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
@Schema(name = "PosterStyleEntity", description = "创作中心图片风格对象")
public class PosterStyleEntity implements java.io.Serializable {

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
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

    /**
     * 该风格下的图片类型变量总数量
     */
    @Schema(description = "该风格下的图片类型变量总数量")
    private Integer totalImageCount;

    /**
     * 素材列表
     */
    @Schema(description = "素材列表")
    private List<? extends AbstractBaseCreativeMaterialDTO> materialList;

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
    private List<PosterTemplateEntity> templateList;

    /**
     * 校验
     */
    public void validate() {
        AppValidate.notEmpty(this.templateList, "请选择海报风格模板！");
        this.templateList.forEach(PosterTemplateEntity::validate);
    }

    /**
     * 固定风格1
     *
     * @return 风格1
     */
    public static PosterStyleEntity ofOne() {
        PosterStyleEntity posterStyle = new PosterStyleEntity();
        posterStyle.setId("1");
        posterStyle.setName("风格 1");
        posterStyle.setEnable(Boolean.TRUE);
        posterStyle.setTemplateList(Collections.singletonList(PosterTemplateEntity.ofMain()));
        return posterStyle;
    }
}
