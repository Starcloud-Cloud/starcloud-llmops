package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterStyleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Deprecated
public class PosterSchemeStepDTO extends BaseSchemeStepDTO {

    private static final long serialVersionUID = 1488877429722884016L;

    /**
     * 创作方案步骤图片风格
     */
    @Schema(description = "创作方案步骤图片风格")
    private List<PosterStyleDTO> styleList;

    /**
     * 校验
     */
    @Override
    public void validate() {
        AppValidate.notEmpty(styleList, "缺少必填项：海报风格不能为空！");
        styleList.forEach(PosterStyleDTO::validate);
    }

}
