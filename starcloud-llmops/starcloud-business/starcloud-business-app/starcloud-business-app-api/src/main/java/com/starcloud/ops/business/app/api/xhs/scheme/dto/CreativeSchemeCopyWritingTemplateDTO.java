package com.starcloud.ops.business.app.api.xhs.scheme.dto;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
@Schema(name = "CreativeSchemeCopyWritingTemplateDTO", description = "文案生成模板")
public class CreativeSchemeCopyWritingTemplateDTO implements java.io.Serializable {

    private static final long serialVersionUID = 2702779004299599670L;

    /**
     * 文案总结信息
     */
    @Schema(description = "文案总结信息")
    @NotBlank(message = "文案总结信息不能为空")
    private String summary;

    /**
     * 生成文案的要求
     */
    @Schema(description = "生成文案要求")
    @NotBlank(message = "生成文案要求不能为空")
    private String demand;

    /**
     * 文案生成模板变量
     */
    @Schema(description = "文案生成模板变量")
    private List<VariableItemDTO> variables;

    /**
     * 校验
     *
     * @param name 方案名称
     */
    public void validate(String name) {
        if (StrUtil.isBlank(summary)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_COPY_WRITING_TEMPLATE_SUMMARY_REQUIRED, name);
        }
        if (StrUtil.isBlank(demand)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_COPY_WRITING_TEMPLATE_DEMAND_REQUIRED, name);
        }
    }
}
