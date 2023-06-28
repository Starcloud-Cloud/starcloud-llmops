package com.starcloud.ops.business.user.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "扫码登录")
@Data
public class ScanLoginRequest {

    @Schema(description = "凭证")
    @NotBlank(message = "凭证不能为空")
    private String ticket;

    @Schema(description = "邀请码")
    private String inviteCode;
}
