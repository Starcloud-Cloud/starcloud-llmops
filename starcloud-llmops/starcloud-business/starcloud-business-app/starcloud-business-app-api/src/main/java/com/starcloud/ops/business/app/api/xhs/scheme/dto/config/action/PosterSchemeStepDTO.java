package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
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
        if (CollectionUtil.isEmpty(styleList)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(720100400, "海报风格不能为空"));
        }
        styleList.forEach(PosterStyleDTO::validate);
    }

    /**
     * 简化
     */
    @Override
    public void easy() {

    }
    
}
