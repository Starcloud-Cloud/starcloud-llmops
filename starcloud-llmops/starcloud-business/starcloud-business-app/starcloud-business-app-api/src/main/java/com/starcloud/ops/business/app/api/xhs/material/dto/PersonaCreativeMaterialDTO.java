package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

@Data
public class PersonaCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @FieldDefine(desc = "头像图片", type = FieldTypeEnum.image)
    private String avatarImageUrl;

    @FieldDefine(desc = "美食图片", type = FieldTypeEnum.image)
    private String gourmetImageUrl;

    @FieldDefine(desc = "风景图片", type = FieldTypeEnum.image)
    private String sceneryImageUrl;

    @Override
    String generateContent() {
        return StrUtil.EMPTY;
    }
}
