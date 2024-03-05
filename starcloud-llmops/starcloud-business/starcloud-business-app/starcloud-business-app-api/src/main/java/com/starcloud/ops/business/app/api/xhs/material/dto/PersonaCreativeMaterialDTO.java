package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import org.apache.tika.utils.StringUtils;

@Data
public class PersonaCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @FieldDefine(fieldName = "avatarImageUrl", desc = "头像图片", type = FieldTypeEnum.image)
    private String avatarImageUrl;

    @FieldDefine(fieldName = "gourmetImageUrl", desc = "美食图片", type = FieldTypeEnum.image)
    private String gourmetImageUrl;

    @FieldDefine(fieldName = "sceneryImageUrl", desc = "风景图片", type = FieldTypeEnum.image)
    private String sceneryImageUrl;

    @Override
    String generateContent() {
        return StringUtils.EMPTY;
    }
}
