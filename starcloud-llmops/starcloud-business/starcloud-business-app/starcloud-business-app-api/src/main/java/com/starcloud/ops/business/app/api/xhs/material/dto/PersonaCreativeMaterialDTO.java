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
public class PersonaCreativeMaterialDTO extends AbstractCreativeMaterialDTO {

    private static final long serialVersionUID = -174052880963691840L;

    @JsonPropertyDescription("头像图片")
    @FieldDefine(desc = "头像图片", type = FieldTypeEnum.image)
    @ExcelProperty("头像图片")
    private String avatarImageUrl;

    @JsonPropertyDescription("美食图片")
    @FieldDefine(desc = "美食图片", type = FieldTypeEnum.image)
    @ExcelProperty("美食图片")
    private String gourmetImageUrl;

    @JsonPropertyDescription("风景图片")
    @FieldDefine(desc = "风景图片", type = FieldTypeEnum.image)
    @ExcelProperty("风景图片")
    private String sceneryImageUrl;

    @Override
    public String generateContent() {
        return StrUtil.EMPTY;
    }

}
