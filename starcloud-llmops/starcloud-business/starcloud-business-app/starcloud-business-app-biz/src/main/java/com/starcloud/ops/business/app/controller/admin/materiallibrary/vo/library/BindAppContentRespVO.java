package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "绑定应用的信息")
public class BindAppContentRespVO {

    @Schema(description = "应用uid")
    private String appUid;

    @Schema(description = "应用来源 APP/MARKET")
    private String source;

    @Schema(description = "应用名称")
    private String appName;

}
