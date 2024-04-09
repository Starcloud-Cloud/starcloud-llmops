package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class OrdinaryNoteTitleMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    private static final long serialVersionUID = 6963607295424278366L;

    @JsonPropertyDescription("标题")
    @FieldDefine(desc = "标题", type = FieldTypeEnum.string, required = true)
    private String title;

    @JsonPropertyDescription("参考来源")
    @FieldDefine(desc = "参考来源", type = FieldTypeEnum.select)
    private String source;

    @JsonPropertyDescription("参考链接地址")
    @FieldDefine(desc = "参考链接地址", type = FieldTypeEnum.weburl)
    private String link;

    @FieldDefine(desc = "模仿元素", type = FieldTypeEnum.listStr)
    private List<String> imitateType;

    @Override
    public String generateContent() {
        return title;
    }

    @Override
    public void clean() {
        this.link = null;
        this.source = null;
        this.imitateType = null;
        super.clean();
    }
}
