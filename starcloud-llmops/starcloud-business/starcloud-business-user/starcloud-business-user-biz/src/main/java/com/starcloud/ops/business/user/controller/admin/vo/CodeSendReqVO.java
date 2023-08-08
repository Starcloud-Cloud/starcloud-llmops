package com.starcloud.ops.business.user.controller.admin.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import com.starcloud.ops.business.user.enums.CommunicationToolsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Schema(description = "发送验证码 Request VO")
@Data
public class CodeSendReqVO {

    @Schema(description = "帐号，手机号或邮箱")
    private String account;

    @Schema(description = "发送场景,对应 SmsSceneEnum 枚举", example = "1")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;


    @Schema(description = "发送工具，手机、email", example = "1")
    @NotNull(message = "发送工具不能为空")
    @InEnum(CommunicationToolsEnum.class)
    private Integer tool;

}
