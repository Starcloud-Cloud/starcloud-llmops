package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import com.starcloud.ops.business.app.api.AppValidate;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class MaterialSchemeStepDTO extends BaseSchemeStepDTO {

    private static final long serialVersionUID = -965407067000703813L;

    /**
     * 创作方案资料库类型
     */
    @Schema(description = "创作方案资料库类型")
    private String materialType;

    /**
     * 校验
     */
    @Override
    public void validate() {
        AppValidate.notBlank(materialType, "缺少必填项：资料库类型！请联系管理员！");
    }
}
