package com.starcloud.ops.business.open.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "添加好友")
public class AddFriendReqVO {

    @Schema(description = "手机号")
    @Mobile
    @NotBlank(message = "手机号不能为空")
    private String mobile;
}
