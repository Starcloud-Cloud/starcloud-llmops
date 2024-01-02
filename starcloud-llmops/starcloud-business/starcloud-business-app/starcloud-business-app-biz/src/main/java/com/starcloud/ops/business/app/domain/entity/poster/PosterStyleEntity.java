package com.starcloud.ops.business.app.domain.entity.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "海报风格对象 VO")
public class PosterStyleEntity implements Serializable {

    private static final long serialVersionUID = 1093411003287488657L;

    /**
     * 海报风格ID
     */
    @Schema(description = "海报风格ID")
    @NotBlank(message = "海报风格ID不能为空")
    private String id;

    /**
     * 海报风格名称
     */
    @Schema(description = "海报风格名称")
    private String name;

    /**
     * 海报风格描述
     */
    @Schema(description = "海报风格描述")
    private String description;

    /**
     * 海报模板列表
     */
    @Schema(description = "海报模板列表")
    @NotEmpty(message = "海报模板列表不能为空")
    @Valid
    private List<PosterTemplateEntity> templateList;

    /**
     * 校验
     */
    public void validate() {
        if (CollectionUtil.isEmpty(this.templateList)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_IMAGE_TEMPLATE_NOT_EXIST);
        }
        for (PosterTemplateEntity template : this.templateList) {
            template.validate();
        }
    }

    /**
     * 组装
     *
     * @param imageType          海报类型
     * @param posterMaterialList 海报素材列表
     * @param posterContent      海报内容
     */
    public void assemble(String imageType, List<String> posterMaterialList, String posterContent) {
        if (CreativeSchemeModeEnum.RANDOM_IMAGE_TEXT.name().equalsIgnoreCase(imageType)) {
            handleRandomImageText(posterMaterialList, posterContent);
            return;
        }
        if (CreativeSchemeModeEnum.PRACTICAL_IMAGE_TEXT.name().equalsIgnoreCase(imageType)) {
            handlePracticalImageText(posterMaterialList, posterContent);
        }

    }

    private void handleRandomImageText(List<String> posterMaterialList, String posterContent) {

    }

    private void handlePracticalImageText(List<String> posterMaterialList, String posterContent) {

    }

}
