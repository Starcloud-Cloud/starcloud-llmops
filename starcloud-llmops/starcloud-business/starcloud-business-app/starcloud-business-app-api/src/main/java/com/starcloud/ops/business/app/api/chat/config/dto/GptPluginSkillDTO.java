package com.starcloud.ops.business.app.api.chat.config.dto;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "gpt插件技能配置")
public class GptPluginSkillDTO extends BasicAbstractSkillsDTO{

    @Schema(description = "插件url")
    private String pluginUrl;


    @Override
    public void valid() {
        super.valid();
        if (StringUtil.isBlank(pluginUrl)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(50000001, "gpt插件技能配置插件url不能为空"));
        }
    }
}
