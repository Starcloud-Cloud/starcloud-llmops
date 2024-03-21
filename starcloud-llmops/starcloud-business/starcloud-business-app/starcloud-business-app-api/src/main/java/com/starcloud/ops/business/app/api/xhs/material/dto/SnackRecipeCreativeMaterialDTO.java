package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
@Accessors(chain = false)
public class SnackRecipeCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    private static final long serialVersionUID = -5645484220659667132L;

    @JsonPropertyDescription("小吃名称")
    @FieldDefine(desc = "小吃名称", type = FieldTypeEnum.string)
    @ExcelProperty("小吃名称")
    private String snackName;

    @JsonPropertyDescription("小吃配料")
    @FieldDefine(desc = "小吃配料", type = FieldTypeEnum.string)
    @ExcelProperty("小吃配料")
    private String ingredients;

    @JsonPropertyDescription("小吃做法")
    @FieldDefine(desc = "小吃做法", type = FieldTypeEnum.string)
    @ExcelProperty("小吃做法")
    private String course;

    @JsonPropertyDescription("生成图片")
    @FieldDefine(desc = "生成图片", type = FieldTypeEnum.image)
    @ExcelProperty("生成图片")
    private String pictureUrl;

    @Override
    public String generateContent() {
        return snackName;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(snackName)) {
            throw exception(MATERIAL_FIELD_NOT_VALID, "小吃名称不能为空");
        }
    }
}
