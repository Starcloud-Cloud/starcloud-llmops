package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class OrdinaryNoteMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    private static final long serialVersionUID = 6000409686906819644L;

    @JsonPropertyDescription("标题")
    @FieldDefine(desc = "标题", type = FieldTypeEnum.string)
    private String title;

    @JsonPropertyDescription("内容")
    @FieldDefine(desc = "内容", type = FieldTypeEnum.string)
    private String content;

    @JsonPropertyDescription("参考来源")
    @FieldDefine(desc = "参考来源", type = FieldTypeEnum.select)
    private String source;

    @JsonPropertyDescription("参考链接地址")
    @FieldDefine(desc = "参考链接地址", type = FieldTypeEnum.weburl)
    private String link;

    @Override
    public String generateContent() {
        return title + "\n" + content;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(content)) {
            throw exception(MATERIAL_FIELD_NOT_VALID, "笔记内容不能为空");
        }
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
