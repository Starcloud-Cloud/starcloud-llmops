package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class OrdinaryNoteTitleMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    private static final long serialVersionUID = 6963607295424278366L;

    @JsonPropertyDescription("标题")
    @FieldDefine(desc = "标题", type = FieldTypeEnum.string)
    private String title;

    @JsonPropertyDescription("参考链接地址")
    @FieldDefine(desc = "参考链接地址", type = FieldTypeEnum.string)
    private String link;

    @JsonPropertyDescription("参考来源")
    @FieldDefine(desc = "参考来源", type = FieldTypeEnum.string)
    private String source;

    @Override
    public String generateContent() {
        return title;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(title)) {
            throw exception(MATERIAL_FIELD_NOT_VALID, "笔记标题不能为空");
        }
    }

    @Override
    public void clean() {
        this.link = null;
        this.source = null;
        super.clean();
    }
}
