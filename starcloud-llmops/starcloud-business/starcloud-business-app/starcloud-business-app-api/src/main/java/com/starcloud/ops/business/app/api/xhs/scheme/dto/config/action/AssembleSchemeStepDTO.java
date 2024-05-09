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
public class AssembleSchemeStepDTO extends BaseSchemeStepDTO {

    private static final long serialVersionUID = 4820280880902279978L;

    /**
     * 创作方案步骤标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 创作方案步骤要求
     */
    @Schema(description = "内容")
    private String content;

    /**
     * 校验
     */
    @Override
    public void validate() {
        AppValidate.notBlank(title, "缺少必填项：内容拼接步骤标题不能为空！");
        AppValidate.notBlank(content, "缺少必填项：内容拼接步骤内容不能为空！");
    }

}
