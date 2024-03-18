package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class OrdinaryNoteMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @FieldDefine(desc = "标题", type = FieldTypeEnum.string)
    private String title;

    @FieldDefine(desc = "内容", type = FieldTypeEnum.string)
    private String content;

    @FieldDefine(desc = "参考链接地址", type = FieldTypeEnum.string)
    private String link;

    @FieldDefine(desc = "参考来源", type = FieldTypeEnum.string)
    private String source;

    @Override
    public String generateContent() {
        return title + "\n" + content;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(content)) {
            throw exception(MATERIAL_FIELD_NOT_VALID,"笔记内容不能为空");
        }
        if (StrUtil.isBlank(title)) {
            throw exception(MATERIAL_FIELD_NOT_VALID,"笔记标题不能为空");
        }
    }

    @Override
    public void clean() {
        this.link = null;
        this.source = null;
        super.clean();
    }
}
