package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "小红书ocr插件请求")
public class XhsOcrReqVO {

    @NotBlank(message = "小红书url必填")
    @Schema(description = "小红书url")
    private String xhsNoteUrl;
}
