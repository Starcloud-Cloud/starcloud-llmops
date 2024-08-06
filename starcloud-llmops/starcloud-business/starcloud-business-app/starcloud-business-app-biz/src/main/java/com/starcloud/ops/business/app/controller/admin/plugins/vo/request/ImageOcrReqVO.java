package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;


import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaOptions;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Schema(description = "图片ocr请求")
public class ImageOcrReqVO {

    @NotEmpty(message = "图片url必填")
    @Schema(name = "图片url", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> imageUrls;


    private Boolean  cleansing;
}
