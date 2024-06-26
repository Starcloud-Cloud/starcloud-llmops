package com.starcloud.ops.business.app.api.ocr;

import lombok.Data;

@Data
public class OcrResult {

    private boolean success;

    private String message;

    private OcrGeneralDTO ocrGeneralDTO;

    private String requestId;
}
