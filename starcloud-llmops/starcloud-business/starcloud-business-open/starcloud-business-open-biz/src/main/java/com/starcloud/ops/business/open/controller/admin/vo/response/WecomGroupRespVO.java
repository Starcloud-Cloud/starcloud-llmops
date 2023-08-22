package com.starcloud.ops.business.open.controller.admin.vo.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "企业微信群明细")
@ToString
public class WecomGroupRespVO {


    @Schema(description = "群名")
    @NotBlank
    private String groupName;

    @Schema(description = "群的群公告")
    private String groupAnnouncement;

    @Schema(description = "群二维码链接")
    private String qrCode;
}
