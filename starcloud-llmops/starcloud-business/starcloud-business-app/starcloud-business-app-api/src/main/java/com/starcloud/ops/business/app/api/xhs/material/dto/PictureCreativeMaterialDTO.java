package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class PictureCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @JsonPropertyDescription("图片")
    @FieldDefine(desc = "图片", type = FieldTypeEnum.image)
    private String pictureUrl;

    @Override
    public String generateContent() {
        return StrUtil.EMPTY;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(pictureUrl)) {
            throw exception(MATERIAL_FIELD_NOT_VALID,"图片不能为空");
        }
    }
}
