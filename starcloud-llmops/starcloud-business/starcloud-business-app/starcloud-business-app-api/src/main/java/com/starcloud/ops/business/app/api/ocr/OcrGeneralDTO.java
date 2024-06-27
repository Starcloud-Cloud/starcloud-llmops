package com.starcloud.ops.business.app.api.ocr;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class OcrGeneralDTO {

    /**
     * 图片地址
     */
    @JsonPropertyDescription("图片地址")
    private String url;

    /**
     * 识别内容
     */
    @JsonPropertyDescription("识别内容")
    private String content;

    /**
     * json 返回数据
     */
    @JsonPropertyDescription("ocr json")
    private String data;

    /**
     * 标签
     */
    @JsonPropertyDescription("标签")
    private String tag;


}
