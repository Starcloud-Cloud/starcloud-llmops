package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Data
public class OrdinaryNoteContentMaterialDTO extends AbstractCreativeMaterialDTO {

    private static final long serialVersionUID = 5329304085500798664L;

    @JsonPropertyDescription("参考来源")
    @FieldDefine(desc = "参考来源", type = FieldTypeEnum.select)
    private String source;

    @JsonPropertyDescription("参考链接地址")
    @FieldDefine(desc = "参考链接地址", type = FieldTypeEnum.weburl)
    private String link;

    @JsonPropertyDescription("内容")
    @FieldDefine(desc = "内容", type = FieldTypeEnum.textBox, required = true)
    private String content;


    @FieldDefine(desc = "模仿元素", type = FieldTypeEnum.listStr)
    private List<String> imitateType;

    @Override
    public String generateContent() {
        return content;
    }

    @Override
    public void clean() {
        this.link = null;
        this.source = null;
        super.clean();
    }

}
