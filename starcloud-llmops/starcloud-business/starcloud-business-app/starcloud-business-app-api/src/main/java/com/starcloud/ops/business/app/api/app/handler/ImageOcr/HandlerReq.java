package com.starcloud.ops.business.app.api.app.handler.ImageOcr;


import com.starcloud.ops.business.app.api.app.annotation.variable.VariableItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "图片ocr插件请求")
public class HandlerReq implements Serializable {

    @NotEmpty(message = "图片url必填")
    @Schema(name = "图片url", title = "图片url", requiredMode = Schema.RequiredMode.REQUIRED)
    @VariableItem
    private List<String> imageUrls;

    @NotBlank(message = "是否清洗OCR内容")
    @Schema(name = "是否清洗OCR内容", title = "是否清洗OCR内容", description = "是否清洗OCR内容", defaultValue = "false")
    @VariableItem
    private Boolean  cleansing;

}
