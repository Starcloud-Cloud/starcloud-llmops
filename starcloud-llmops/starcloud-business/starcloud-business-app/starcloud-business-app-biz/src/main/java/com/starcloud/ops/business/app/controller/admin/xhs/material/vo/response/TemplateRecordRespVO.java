package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模板使用记录")
public class TemplateRecordRespVO {

    @Schema(description = "记录uid")
    private String uid;

    @Schema(description = "模板code")
    private String templateCode;

    @Schema(description = "图片模板名称")
    private String name;

    @Schema(description = "示例图片")
    private String example;
}
