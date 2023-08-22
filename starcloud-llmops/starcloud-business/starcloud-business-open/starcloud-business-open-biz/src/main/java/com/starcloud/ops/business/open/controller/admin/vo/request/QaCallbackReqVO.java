package com.starcloud.ops.business.open.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@Schema(description = "worktool平台消息回调")
@ToString
public class QaCallbackReqVO {

    @Schema(description = "消息文本", example = "你好")
    private String spoken;

    @Schema(description = "原始消息文本", example = "@me 你好")
    private String rawSpoken;

    @Schema(description = "发消息者名称")
    private String receivedName;

    @Schema(description = "所在群名称")
    private String groupName;

    @Schema(description = "群备注名")
    private String groupRemark;

    @Schema(description = "所在群类型 1=外部群 2=外部联系人 3=内部群 4=内部联系人")
    private Integer roomType;

    @Schema(description = "群聊中是否@me")
    private Boolean atMe;

    @Schema(description = "消息类型 0=未知 1=文本 2=图片 5=视频 7=小程序 8=链接 9=文件")
    private Integer textType;


}
