package com.starcloud.ops.business.user.controller.admin.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "公共号二维码")
public class QrCodeTicketVO {

    @Schema(description = "二维码凭证")
    private String ticket;

    @Schema(description = "二维码链接")
    private String url;

    @Schema(description = "有效期 -1为永久有效")
    private int expireSeconds ;
}
