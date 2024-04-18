package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = false)
public class TravelGuideCreativeMaterialDTO extends AbstractCreativeMaterialDTO{

    @JsonPropertyDescription("景区")
    @FieldDefine(desc = "景区", type = FieldTypeEnum.string)
    @ExcelProperty("景区")
    private String scenicSpot;

    @JsonPropertyDescription("美食")
    @FieldDefine(desc = "美食", type = FieldTypeEnum.string)
    @ExcelProperty("美食")
    private String delicacies;

    @JsonPropertyDescription("人群")
    @FieldDefine(desc = "人群", type = FieldTypeEnum.string)
    @ExcelProperty("人群")
    private String crowd;

    @JsonPropertyDescription("网红店")
    @FieldDefine(desc = "网红店", type = FieldTypeEnum.string)
    @ExcelProperty("网红店")
    private String influenceShop;

    @JsonPropertyDescription("图片1")
    @FieldDefine(desc = "图片1", type = FieldTypeEnum.image)
    @ExcelProperty("图片1")
    private String imageOne;

    @JsonPropertyDescription("图片2")
    @FieldDefine(desc = "图片2", type = FieldTypeEnum.image)
    @ExcelProperty("图片2")
    private String imageTwo;

    @JsonPropertyDescription("图片3")
    @FieldDefine(desc = "图片3", type = FieldTypeEnum.image)
    @ExcelProperty("图片3")
    private String imageThree;

    @JsonPropertyDescription("图片4")
    @FieldDefine(desc = "图片4", type = FieldTypeEnum.image)
    @ExcelProperty("图片4")
    private String imageFour;

    @Override
    public String generateContent() {
        return null;
    }
}
