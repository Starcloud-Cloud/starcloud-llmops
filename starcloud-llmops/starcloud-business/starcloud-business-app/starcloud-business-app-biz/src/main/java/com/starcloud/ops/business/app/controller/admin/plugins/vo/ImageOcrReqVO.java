package com.starcloud.ops.business.app.controller.admin.plugins.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "图片ocr插件请求")
public class ImageOcrReqVO {

    @NotBlank(message = "图片url必填")
    @Schema(description = "图片url")
    private String imageUrl;
}
