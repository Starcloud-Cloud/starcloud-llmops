package com.starcloud.ops.business.mission.api.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "绑定分组")
public class WechatUserBindReqVO {

    @NotNull(message = "邀请码不能为空")
    @Schema(description = "邀请码")
    private String inviteCode;
}
