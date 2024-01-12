package com.starcloud.ops.business.app.service.xhs.scheme.entity.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
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
     * 模板列表
     */
    @Schema(description = "模板列表")
    @Valid
    @NotEmpty(message = "请选择图片模板！")
    private List<PosterTemplateEntity> templateList;

    /**
     * 固定风格1
     *
     * @return 风格1
     */
    public static PosterStyleEntity ofOne() {
        PosterStyleEntity posterStyle = new PosterStyleEntity();
        posterStyle.setId("STYLE_1");
        posterStyle.setName("风格 1");
        posterStyle.setTemplateList(Collections.singletonList(PosterTemplateEntity.ofMain()));
        return posterStyle;
    }

}
