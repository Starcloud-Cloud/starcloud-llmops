package com.starcloud.ops.business.app.api.xhs.material;


import cn.hutool.core.bean.BeanPath;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Schema(description = "小红书ocr结果")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XhsNoteDTO {

    @JsonPropertyDescription("笔记id")
    @Schema(description = "笔记id")
    private String noteId;

    @JsonPropertyDescription("标题")
    @Schema(description = "标题")
    private String title;

    @JsonPropertyDescription("内容")
    @Schema(description = "内容")
    private String content;

    @JsonPropertyDescription("标签 多标签逗号分割")
    @Schema(description = "标签 多标签逗号分割")
    private String tags;

    @JsonPropertyDescription("图片1")
    @Schema(description = "图片1")
    private OcrGeneralDTO image1;

    @JsonPropertyDescription("图片2")
    @Schema(description = "图片2")
    private OcrGeneralDTO image2;

    @JsonPropertyDescription("图片3")
    @Schema(description = "图片3")
    private OcrGeneralDTO image3;

    @JsonPropertyDescription("图片4")
    @Schema(description = "图片4")
    private OcrGeneralDTO image4;

    @JsonPropertyDescription("图片5")
    @Schema(description = "图片5")
    private OcrGeneralDTO image5;

    @JsonPropertyDescription("图片6")
    @Schema(description = "图片6")
    private OcrGeneralDTO image6;

    @JsonPropertyDescription("图片7")
    @Schema(description = "图片7")
    private OcrGeneralDTO image7;

    @JsonPropertyDescription("图片8")
    @Schema(description = "图片8")
    private OcrGeneralDTO image8;

    @JsonPropertyDescription("图片9")
    @Schema(description = "图片9")
    private OcrGeneralDTO image9;

    @JsonPropertyDescription("图片10")
    @Schema(description = "图片10")
    private OcrGeneralDTO image10;

    /**
     * 填充图片地址
     * @param urls
     */
    public void addImage(List<String> urls) {
        for (int i = 0; i < urls.size() && i < 10; i++) {
            BeanPath beanPath = new BeanPath("image" + (i + 1));
            OcrGeneralDTO ocrGeneralDTO = new OcrGeneralDTO();
            ocrGeneralDTO.setUrl(urls.get(i));
            beanPath.set(this, ocrGeneralDTO);
        }
    }

    public List<OcrGeneralDTO> listOcrDTO() {
        List<OcrGeneralDTO> ocrDTOList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            BeanPath beanPath = new BeanPath("image" + i);
            OcrGeneralDTO o = (OcrGeneralDTO) beanPath.get(this);
            if (Objects.isNull(o)) {
                return ocrDTOList;
            }
            ocrDTOList.add(o);
        }
        return ocrDTOList;
    }
}
