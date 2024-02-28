package com.starcloud.ops.business.app.api.xhs.scheme.dto.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
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
     * 图片数量
     */
    @Schema(description = "图片数量")
    private Integer imageCount;

    /**
     * 最大图片数量
     */
    @Schema(description = "最大图片数量")
    private Integer maxImageCount;

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

    public void validate() {
        if (StrUtil.isBlank(id)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(720100400, "风格ID不能为空"));
        }
        if (CollectionUtil.isEmpty(templateList)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(720100401, "请选择图片模板！"));
        }
        templateList.forEach(PosterTemplateDTO::validate);
    }

}
