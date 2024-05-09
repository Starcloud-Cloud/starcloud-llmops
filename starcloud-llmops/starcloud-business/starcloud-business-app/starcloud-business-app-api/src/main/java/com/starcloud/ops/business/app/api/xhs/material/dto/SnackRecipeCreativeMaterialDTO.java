package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Data
@Accessors(chain = false)
public class SnackRecipeCreativeMaterialDTO extends AbstractCreativeMaterialDTO {

    private static final long serialVersionUID = -5645484220659667132L;

    @JsonPropertyDescription("小吃名称")
    @FieldDefine(desc = "小吃名称", type = FieldTypeEnum.string, required = true)
    @ExcelProperty("小吃名称")
    private String snackName;

    @JsonPropertyDescription("小吃图片")
    @FieldDefine(desc = "生成图片", type = FieldTypeEnum.image)
    @ExcelProperty("生成图片")
    private String pictureUrl;

    @JsonPropertyDescription("小吃配料")
    @FieldDefine(desc = "小吃配料", type = FieldTypeEnum.textBox)
    @ExcelProperty("小吃配料")
    private String ingredients;

    @JsonPropertyDescription("小吃做法")
    @FieldDefine(desc = "小吃做法", type = FieldTypeEnum.textBox)
    @ExcelProperty("小吃做法")
    private String course;


    @Override
    public String generateContent() {
        return snackName;
    }

}
