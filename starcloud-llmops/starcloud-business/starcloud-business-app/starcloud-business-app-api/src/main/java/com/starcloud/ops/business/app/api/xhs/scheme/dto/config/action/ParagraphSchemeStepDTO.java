package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ParagraphSchemeStepDTO extends StandardSchemeStepDTO {

    private static final long serialVersionUID = 6843541753056072604L;

    /**
     * 创作方案步骤生成的段落数
     */
    @Schema(description = "创作方案步骤生成的段落数")
    private Integer paragraphCount;

    /**
     * 校验
     */
    @Override
    public void validate() {
        super.validate();
        if (Objects.isNull(paragraphCount) || paragraphCount <= 0) {
            throw ServiceExceptionUtil.exception(new ErrorCode(720100400, "段落数不能为空或者不能小于0！"));
        }
    }

    /**
     * 简化
     */
    @Override
    public void easy() {
        super.easy();
        this.paragraphCount = null;
    }

}
