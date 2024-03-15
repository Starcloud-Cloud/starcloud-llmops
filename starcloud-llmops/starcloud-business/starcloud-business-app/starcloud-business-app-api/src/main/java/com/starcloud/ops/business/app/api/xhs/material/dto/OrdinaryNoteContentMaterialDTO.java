package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class OrdinaryNoteContentMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @FieldDefine(desc = "内容", type = FieldTypeEnum.string)
    private String content;

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

    @Override
    public void clean() {
        this.link = null;
        super.clean();
    }

}
