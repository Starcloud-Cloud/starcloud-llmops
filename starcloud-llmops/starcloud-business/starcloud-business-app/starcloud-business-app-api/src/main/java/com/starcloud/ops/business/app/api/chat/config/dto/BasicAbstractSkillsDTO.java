package com.starcloud.ops.business.app.api.chat.config.dto;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class BasicAbstractSkillsDTO implements BaseExpandConfigDTO{

    @Schema(description = "技能名称")
    private String name;

    @Schema(description = "技能简介")
    private String desc;

    @Schema(description = "头像")
    private String icon;

    @Schema(description = "提示文案")
    private String copyWriting;

    @Override
    public void valid() {
        if (StringUtil.isBlank(name)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(50000001, "技能名称不能为空"));
        }
        if (StringUtil.isBlank(desc)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(50000001, "技能简介不能为空"));
        }
    }
}
