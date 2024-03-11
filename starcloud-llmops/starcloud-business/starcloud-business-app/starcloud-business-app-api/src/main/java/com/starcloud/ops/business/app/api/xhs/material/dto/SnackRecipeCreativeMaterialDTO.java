package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class SnackRecipeCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO{

    @FieldDefine(desc = "小吃名称", type = FieldTypeEnum.string)
    private String snackName;

    @FieldDefine(desc = "小吃配料", type = FieldTypeEnum.string)
    private String ingredients;

    @FieldDefine(desc = "小吃做法", type = FieldTypeEnum.string)
    private String course;

    @FieldDefine(desc = "生成图片", type = FieldTypeEnum.image)
    private String pictureUrl;

    @Override
    public String generateContent() {
        return snackName;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(snackName)) {
            throw exception(MATERIAL_FIELD_NOT_VALID,"小吃名称不能为空");
        }
    }
}
