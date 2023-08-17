package com.starcloud.ops.business.user.controller.admin.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.framework.common.validation.Mobile;
import com.starcloud.ops.business.user.enums.CommunicationToolsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "手机号注册 Request VO")
@Data
public class CodeRegisterReqVO {

    @Schema(description = "手机号", required = true)
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String account;

    @Schema(description = "手机验证码", required = true, example = "1024")
    @NotEmpty(message = "手机验证码不能为空")
    @Length(min = 4, max = 6, message = "手机验证码长度为 4-6 位")
    @Pattern(regexp = "^[0-9]+$", message = "手机验证码必须都是数字")
    private String code;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "用户名格式为数字以及字母")
    private String username;

    @Schema(description = "密码")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "发送工具，手机、email", example = "1")
    @NotNull(message = "发送工具不能为空")
    @InEnum(CommunicationToolsEnum.class)
    private Integer tool;
}
