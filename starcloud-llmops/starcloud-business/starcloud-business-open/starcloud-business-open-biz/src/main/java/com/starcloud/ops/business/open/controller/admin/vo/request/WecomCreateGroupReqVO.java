package com.starcloud.ops.business.open.controller.admin.vo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "新建企业微信群")
@ToString
public class WecomCreateGroupReqVO {

    @Schema(description = "应用uid")
    @NotBlank
    private String appUid;

    @Schema(description = "群名")
    @NotBlank
    private String groupName;

    @Schema(description = "群的群公告")
    private String groupAnnouncement;

}
