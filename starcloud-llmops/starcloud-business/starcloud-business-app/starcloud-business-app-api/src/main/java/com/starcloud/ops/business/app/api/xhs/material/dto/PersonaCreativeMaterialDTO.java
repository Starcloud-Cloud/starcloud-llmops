package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

@Data
public class PersonaCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @JsonPropertyDescription("头像图片")
    @FieldDefine(desc = "头像图片", type = FieldTypeEnum.image)
    private String avatarImageUrl;

    @JsonPropertyDescription("美食图片")
    @FieldDefine(desc = "美食图片", type = FieldTypeEnum.image)
    private String gourmetImageUrl;

    @JsonPropertyDescription("风景图片")
    @FieldDefine(desc = "风景图片", type = FieldTypeEnum.image)
    private String sceneryImageUrl;

    @Override
    public String generateContent() {
        return StrUtil.EMPTY;
    }

    @Override
    public void valid() {

    }
}
