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
@Schema(description = "应用技能配置")
public class AppWorkflowSkillDTO extends BasicAbstractSkillsDTO{


    @Schema(description = "描述 prompt")
    private String defaultPromptDesc;

    @Schema(description = "绑定的appUid")
    private String skillAppUid;

    @Override
    public void valid() {
        super.valid();
        if (StringUtil.isBlank(skillAppUid)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(50000001, "应用技能配置绑定的appUid不能为空"));
        }
    }
}
