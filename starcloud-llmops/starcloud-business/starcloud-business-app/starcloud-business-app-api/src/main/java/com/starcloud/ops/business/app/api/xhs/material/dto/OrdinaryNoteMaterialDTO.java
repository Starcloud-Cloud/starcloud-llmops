package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Data
public class OrdinaryNoteMaterialDTO extends AbstractCreativeMaterialDTO {

    private static final long serialVersionUID = 6000409686906819644L;

    @JsonPropertyDescription("参考来源")
    @FieldDefine(desc = "参考来源", type = FieldTypeEnum.select)
    private String source;

    @JsonPropertyDescription("参考链接地址")
    @FieldDefine(desc = "参考链接地址", type = FieldTypeEnum.weburl)
    private String link;

    @JsonPropertyDescription("标题")
    @FieldDefine(desc = "标题", type = FieldTypeEnum.string, required = true, width = 400)
    private String title;

    @JsonPropertyDescription("内容")
    @FieldDefine(desc = "内容", type = FieldTypeEnum.textBox, required = true, width = 400)
    private String content;

    @FieldDefine(desc = "图片", type = FieldTypeEnum.listImage)
    private List<String> images;

    @FieldDefine(desc = "标签", type = FieldTypeEnum.listStr)
    private List<String> tags;

    @FieldDefine(desc = "模仿元素", type = FieldTypeEnum.listStr)
    private List<String> imitateType;

    @Override
    public String generateContent() {
        return title + "\n" + content;
    }

    @Override
    public void clean() {
        this.link = null;
        this.source = null;
        super.clean();
    }
}
