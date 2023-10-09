package com.starcloud.ops.business.open.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "绑定微信群聊")
public class WeChatBindReqVO {

    @Schema(description = "机器人 Uid")
    @NotBlank(message = "机器人 Uid 不能为空")
    private String appUid;

    @Schema(description = "发布 Uid")
    @NotBlank(message = "发布 Uid 不能为空")
    private String publishUid;

    @Schema(description = "公共号名称")
    @NotBlank(message = "公共号名称 不能为空")
    private String name;

    @Schema(description = "微信 开发者id")
    @NotBlank(message = "开发者id 不能为空")
    private String appId;

    @Schema(description = "微信 开发者密码")
    @NotBlank(message = "开发者密码 不能为空")
    private String appSecret;

    @Schema(description = "公众号微信号-自动回复使用")
    @NotBlank(message = "微信号 不能为空")
    private String account;

}
