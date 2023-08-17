package com.starcloud.ops.business.open.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "群二维码回调")
public class GroupCallbackReqVO {

    @Schema(description = "错误码，0为成功 其他为失败")
    private long errorCode;

    @Schema(description = "错误原因")
    private String errorReason;

    @Schema(description = "失败名单，成功时不提供")
    private List<String> failList;

    @Schema(description = "群名")
    private String groupName;

    @Schema(description = "消息id")
    private String messageId;

    @Schema(description = "群二维码链接")
    private String qrCode;

    @Schema(description = "原始指令")
    private String rawMsg;

    @Schema(description = "执行时间，执行时间戳(毫秒)")
    private Long runTime;

    @Schema(description = "成功名单，成功时不提供")
    private List<String> successList;

    @Schema(description = "耗时，指令执行耗时")
    private Double timeCost;

    @Schema(description = "指令类型，指令类型")
    private Long type;

}
