package com.starcloud.ops.business.app.api.ocr;

import lombok.Data;

@Data
public class ImageOcrDTO {

    /**
     * 素材字段key
     */
    private String fieldName;

    /**
     * 字段值
     */
    private String value;

    /**
     * ocr解析内容
     */
    private OcrGeneralDTO ocrGeneralDTO;

}
