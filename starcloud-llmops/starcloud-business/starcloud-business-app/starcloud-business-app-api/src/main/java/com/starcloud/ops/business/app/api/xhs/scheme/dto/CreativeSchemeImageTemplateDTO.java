package com.starcloud.ops.business.app.api.xhs.scheme.dto;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
@Schema(name = "CreativeSchemeImageTemplateDTO", description = "图片生成模板")
public class CreativeSchemeImageTemplateDTO implements java.io.Serializable {

    private static final long serialVersionUID = 1330559953484705125L;

    /**
     * 图片生成模板：图片风格
     */
    @Valid
    @Schema(description = "图片生成风格List")
    private List<PosterStyleDTO> styleList;

    /**
     * 校验
     *
     * @param name 方案名称
     */
    public void validate(String name) {
        if (CollectionUtil.isEmpty(styleList)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_IMAGE_TEMPLATE_STYLE_LIST_NOT_EMPTY, name);
        }
    }
}
