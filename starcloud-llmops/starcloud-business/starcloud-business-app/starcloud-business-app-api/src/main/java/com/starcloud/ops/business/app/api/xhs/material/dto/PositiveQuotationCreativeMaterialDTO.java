package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

@Data
public class PositiveQuotationCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO{

    private static final long serialVersionUID = 8909813313202932654L;

    @JsonPropertyDescription("标题")
    @FieldDefine(desc = "标题", type = FieldTypeEnum.string)
    private String title;

    @JsonPropertyDescription("背景图")
    @FieldDefine(desc = "背景图", type = FieldTypeEnum.image)
    private String backGroundPicUrl;

    @JsonPropertyDescription("语句1")
    @FieldDefine(desc = "语句1", type = FieldTypeEnum.image)
    private String quotationOne;

    @JsonPropertyDescription("语句2")
    @FieldDefine(desc = "语句2", type = FieldTypeEnum.image)
    private String quotationTwo;

    @JsonPropertyDescription("语句3")
    @FieldDefine(desc = "语句3", type = FieldTypeEnum.image)
    private String quotationThree;

    @JsonPropertyDescription("语句4")
    @FieldDefine(desc = "语句4", type = FieldTypeEnum.image)
    private String quotationFour;

    @JsonPropertyDescription("语句5")
    @FieldDefine(desc = "语句5", type = FieldTypeEnum.image)
    private String quotationFive;

    @Override
    public String generateContent() {
        return title;
    }

    @Override
    public void valid() {

    }
}
