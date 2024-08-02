package com.starcloud.ops.business.app.api.ocr;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ocr结果")
public class OcrGeneralDTO {

    /**
     * 图片地址
     */
    @Schema(description = "图片地址")
    @JsonPropertyDescription("图片地址")
    private String url;

    /**
     * 识别内容
     */
    @Schema(description = "识别内容")
    @JsonPropertyDescription("识别内容")
    private String content;


    /**
     * 清洗后的内容
     */
    @Schema(description = "清洗后的内容")
    @JsonPropertyDescription("清洗后的内容")
    private String cleansingContent;

    /**
     * json 返回数据
     */
    @Schema(description = "返回数据json")
    @JsonPropertyDescription("ocr json")
    private String data;

    /**
     * 标签
     */
    @Schema(description = "标签")
    @JsonPropertyDescription("标签")
    private String tag;


}
