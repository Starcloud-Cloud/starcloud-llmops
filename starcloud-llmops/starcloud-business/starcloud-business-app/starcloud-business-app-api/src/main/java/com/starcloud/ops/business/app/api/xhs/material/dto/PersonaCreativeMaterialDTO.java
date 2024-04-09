package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = false)
public class PersonaCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    private static final long serialVersionUID = -174052880963691840L;

    @JsonPropertyDescription("人物图片")
    @FieldDefine(desc = "人物图片", type = FieldTypeEnum.image, required = true)
    @ExcelProperty("人物图片")
    private String avatarImageUrl;

    @JsonPropertyDescription("美食图片")
    @FieldDefine(desc = "美食图片", type = FieldTypeEnum.image, required = true)
    @ExcelProperty("美食图片")
    private String gourmetImageUrl;

    @JsonPropertyDescription("风景图片")
    @FieldDefine(desc = "风景图片", type = FieldTypeEnum.image, required = true)
    @ExcelProperty("风景图片")
    private String sceneryImageUrl;

    @JsonPropertyDescription("运动图片")
    @FieldDefine(desc = "运动图片", type = FieldTypeEnum.image, required = true)
    @ExcelProperty("运动图片")
    private String sportsImageUrl;

    @JsonPropertyDescription("账户截图图片")
    @FieldDefine(desc = "账户截图图片", type = FieldTypeEnum.image, required = true)
    @ExcelProperty("账户截图图片")
    private String moneyImageUrl;

    @JsonPropertyDescription("其他照片1")
    @FieldDefine(desc = "其他照片1", type = FieldTypeEnum.image)
    @ExcelProperty("其他照片1")
    private String others1ImageUrl;

    @JsonPropertyDescription("其他照片2")
    @FieldDefine(desc = "其他照片2", type = FieldTypeEnum.image)
    @ExcelProperty("其他照片2")
    private String others2ImageUrl;


    @JsonPropertyDescription("其他照片3")
    @FieldDefine(desc = "其他照片3", type = FieldTypeEnum.image)
    @ExcelProperty("其他照片3")
    private String others3ImageUrl;

    @JsonPropertyDescription("其他照片4")
    @FieldDefine(desc = "其他照片4", type = FieldTypeEnum.image)
    @ExcelProperty("其他照片4")
    private String others4ImageUrl;

    @Override
    public String generateContent() {
        return StrUtil.EMPTY;
    }

}
