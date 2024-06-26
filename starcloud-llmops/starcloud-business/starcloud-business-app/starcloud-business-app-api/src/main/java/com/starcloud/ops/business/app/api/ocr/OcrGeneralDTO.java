package com.starcloud.ops.business.app.api.ocr;

import lombok.Data;

@Data
public class OcrGeneralDTO {

    /**
     * 图片地址
     */
    private String url;

    /**
     * 识别内容
     */
    private String content;

    /**
     * json 返回数据
     */
    private String data;

    /**
     * 标签
     */
    private String tag;


}
