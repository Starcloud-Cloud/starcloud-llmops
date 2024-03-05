package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import org.apache.tika.utils.StringUtils;

@Data
public class PictureCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @FieldDefine(fieldName = "pictureOneUrl", desc = "图片1", type = FieldTypeEnum.image)
    private String pictureOneUrl;

    @FieldDefine(fieldName = "pictureTwoUrl", desc = "图片2", type = FieldTypeEnum.image)
    private String pictureTwoUrl;

    @FieldDefine(fieldName = "pictureThreeUrl", desc = "图片3", type = FieldTypeEnum.image)
    private String pictureThreeUrl;

    @Override
    String generateContent() {
        return StringUtils.EMPTY;
    }
}
