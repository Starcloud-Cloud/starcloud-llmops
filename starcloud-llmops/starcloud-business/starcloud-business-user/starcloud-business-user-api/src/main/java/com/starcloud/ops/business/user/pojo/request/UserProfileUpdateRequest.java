package com.starcloud.ops.business.user.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Schema(description = "用户个人信息更新 Request")
@Data
public class UserProfileUpdateRequest {

    @Schema(description = "用户名", required = true, example = "芋艿")
    @Length(max = 16, message = "账号长度为 4-16 位")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "账号格式为数字以及字母")
    private String username;

    @Schema(description = "用户昵称", required = true, example = "芋艿")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickname;

    @Schema(description = "用户邮箱", example = "yudao@iocoder.cn")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    @Schema(description = "手机号码", example = "15601691300")
    private String mobile;

    @Schema(description = "用户性别-参见 SexEnum 枚举类", example = "1")
    private Integer sex;

    @Schema(description = "头像")
    private String avatar;
}
