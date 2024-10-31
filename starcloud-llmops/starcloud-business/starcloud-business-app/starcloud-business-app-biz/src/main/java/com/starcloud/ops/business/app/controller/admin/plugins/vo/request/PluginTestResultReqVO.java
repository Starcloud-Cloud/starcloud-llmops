package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import com.starcloud.ops.business.app.enums.plugin.PlatformEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "请求验证结果")
public class PluginTestResultReqVO {

    @NotBlank(message = "实现方式不能为空")
    @InEnum(value = PlatformEnum.class, field = InEnum.EnumField.CODE, message = "实现类型[{value}]必须在: [{values}] 范围内！")
    private String type;

    String code;

    String accessTokenId;
}
