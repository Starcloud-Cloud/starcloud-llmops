package com.starcloud.ops.business.user.pojo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "帐号注册")
public class RegisterRequest {

    @Schema(description = "邮箱地址")
    @NotBlank(message = "Email不能为空")
    private String email;

    @Schema(description = "用户名")
    @NotBlank(message = "username不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "账号格式为数字以及字母")
    private String username;

    @Schema(description = "密码")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

    @Schema(description = "邀请码")
    private String inviteCode;
}
