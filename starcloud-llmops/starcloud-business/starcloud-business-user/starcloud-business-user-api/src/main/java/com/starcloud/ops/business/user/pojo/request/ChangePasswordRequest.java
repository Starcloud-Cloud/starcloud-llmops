package com.starcloud.ops.business.user.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "修改密码")
public class ChangePasswordRequest {

    @Schema(description = "邮箱验证码")
    @NotBlank(message = "verificationCode不能为空")
    private String verificationCode;

    @Schema(description = "新密码")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String newPassword;

}
