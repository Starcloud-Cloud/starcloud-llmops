package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Data
@Accessors(chain = false)
public class PictureCreativeMaterialDTO extends AbstractCreativeMaterialDTO {

    private static final long serialVersionUID = 2659140387627230195L;

    @JsonPropertyDescription("图片")
    @FieldDefine(desc = "图片", type = FieldTypeEnum.image)
    @ExcelProperty("图片")
    private String pictureUrl;

    @Override
    public String generateContent() {
        return StrUtil.EMPTY;
    }

}
