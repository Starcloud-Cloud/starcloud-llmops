package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

@Data
public class PositiveQuotationCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO{

    @FieldDefine(desc = "标题", type = FieldTypeEnum.string)
    private String title;

    @FieldDefine(desc = "背景图", type = FieldTypeEnum.image)
    private String backGroundPicUrl;

    @FieldDefine(desc = "语句1", type = FieldTypeEnum.image)
    private String quotationOne;

    @FieldDefine(desc = "语句2", type = FieldTypeEnum.image)
    private String quotationTwo;

    @FieldDefine(desc = "语句3", type = FieldTypeEnum.image)
    private String quotationThree;

    @FieldDefine(desc = "语句4", type = FieldTypeEnum.image)
    private String quotationFour;

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
