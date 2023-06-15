package com.starcloud.ops.business.user.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "忘记密码")
public class RecoverPasswordRequest {

    @Schema(description = "邮箱地址")
    @Size(min = 5, max = 50, message = "邮箱长度为 5-50个字符")
    @Email(message = "邮箱格式不正确")
    private String email;
}
