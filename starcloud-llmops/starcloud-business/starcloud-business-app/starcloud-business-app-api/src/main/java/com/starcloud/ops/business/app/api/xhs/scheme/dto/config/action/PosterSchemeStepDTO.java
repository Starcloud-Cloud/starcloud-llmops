package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    //    /**
//     * 创作方案步骤图片风格
//     */
//    @Schema(description = "创作方案步骤图片风格")
//    private List<PosterStyleDTO> styleList;
//
//    /**
//     * 校验
//     */
//    @Override
    public void validate() {
    }

}
