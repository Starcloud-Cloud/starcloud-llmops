package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
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
     * 创作方案步骤要求
     */
    @Schema(description = "创作方案步骤要求")
    private String requirement;

    /**
     * 校验
     */
    @Override
    public void validate() {
        if (StrUtil.isBlank(requirement)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(720100400, "文案拼接配置不能为空！"));
        }
    }

    /**
     * 简化
     */
    @Override
    public void easy() {

    }

}
