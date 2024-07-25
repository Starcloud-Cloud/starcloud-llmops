package com.starcloud.ops.business.app.api.ocr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ocr请求结果")
public class OcrResult {

    @Schema(name = "是否成功")
    private boolean success;

    @Schema(name = "错误信息")
    private String message;

    @Schema(name = "图片URL")
    private String url;

    private OcrGeneralDTO ocrGeneralDTO;

    @Schema(name = "请求id")
    private String requestId;
}
