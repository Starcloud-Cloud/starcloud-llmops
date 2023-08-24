package com.starcloud.ops.business.open.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "添加好友")
public class AddFriendReqVO {

    @Schema(description = "手机号")
    @Mobile
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "应用 Uid")
    @NotBlank(message = "应用 Uid 不能为空")
    private String appUid;

    @Schema(description = "发布 Uid")
    @NotBlank(message = "发布 Uid 不能为空")
    private String publishUid;

    @Schema(description = "发布渠道名称")
    @NotBlank(message = "发布渠道名称不能为空")
    private String name;

}
