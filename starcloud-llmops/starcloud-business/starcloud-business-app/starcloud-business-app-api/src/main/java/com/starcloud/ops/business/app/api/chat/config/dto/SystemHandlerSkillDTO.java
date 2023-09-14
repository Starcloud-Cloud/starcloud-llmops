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
@Schema(description = "系统技能配置")
public class SystemHandlerSkillDTO extends BasicAbstractSkillsDTO {

    @Schema(description = "技能code")
    private String code;

    @Override
    public void valid() {
        super.valid();
        if (StringUtil.isBlank(code)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(50000001, "系统技能配置技能code不能为空"));
        }
    }
}
