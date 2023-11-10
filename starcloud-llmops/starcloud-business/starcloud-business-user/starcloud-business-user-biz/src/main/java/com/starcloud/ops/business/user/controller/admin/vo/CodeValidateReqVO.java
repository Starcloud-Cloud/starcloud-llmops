package com.starcloud.ops.business.user.controller.admin.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import com.starcloud.ops.business.user.enums.CommunicationToolsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "验证验证码 Request VO")
@Data
public class CodeValidateReqVO {

    @Schema(description = "帐号，手机号或邮箱")
    private String account;

    @Schema(description = "验证码", required = true, example = "1024")
    @NotEmpty(message = "验证码不能为空")
    @Length(min = 4, max = 6, message = "验证码长度为 4-6 位")
    @Pattern(regexp = "^[0-9]+$", message = "验证码必须都是数字")
    private String code;

    @Schema(description = "发送工具，手机 2、email 1", example = "1")
    @NotNull(message = "发送工具不能为空")
    @InEnum(CommunicationToolsEnum.class)
    private Integer tool;

    @Schema(description = "发送场景,对应 SmsSceneEnum 枚举", example = "1")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;
}
