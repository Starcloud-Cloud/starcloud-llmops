package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class OrdinaryNoteContentMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @FieldDefine(desc = "内容", type = FieldTypeEnum.string)
    private String content;

    @FieldDefine(desc = "参考链接", type = FieldTypeEnum.string)
    private String link;

    @Override
    public String generateContent() {
        return content;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(content)) {
            throw exception(MATERIAL_FIELD_NOT_VALID,"笔记内容不能为空");
        }
    }
}
