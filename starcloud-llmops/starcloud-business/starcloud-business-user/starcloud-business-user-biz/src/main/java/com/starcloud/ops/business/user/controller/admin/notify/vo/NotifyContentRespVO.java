package com.starcloud.ops.business.user.controller.admin.notify.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "通知内容")
public class NotifyContentRespVO {

    @Schema(description = "收信人id")
    private Long receiverId;

    @Schema(description = "收信人名称")
    private String receiverName;

    @Schema(description = "通知内容参数")
    private Map<String,Object> templateParams;

    @Schema(description = "通知内容")
    private String content;
}
