package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
public class ContractCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO{

    private static final long serialVersionUID = -3928689367907211655L;

    @JsonPropertyDescription("合同名称")
    @FieldDefine(desc = "合同名称", type = FieldTypeEnum.string)
    private String name;

    @JsonPropertyDescription("文档图片1")
    @FieldDefine(desc = "文档图片1", type = FieldTypeEnum.image)
    private String documentPicUrlOne;

    @JsonPropertyDescription("文档图片2")
    @FieldDefine(desc = "文档图片2", type = FieldTypeEnum.image)
    private String documentPicUrlTwo;

    @JsonPropertyDescription("文档图片3")
    @FieldDefine(desc = "文档图片3", type = FieldTypeEnum.image)
    private String documentPicUrlThree;

    @JsonPropertyDescription("文档图片4")
    @FieldDefine(desc = "文档图片4", type = FieldTypeEnum.image)
    private String documentPicUrlFour;

    @JsonPropertyDescription("文档图片5")
    @FieldDefine(desc = "文档图片5", type = FieldTypeEnum.image)
    private String documentPicUrlFive;

    @JsonPropertyDescription("文档图片6")
    @FieldDefine(desc = "文档图片6", type = FieldTypeEnum.image)
    private String documentPicUrlSix;

    @JsonPropertyDescription("合同编号")
    @FieldDefine(desc = "编号", type = FieldTypeEnum.integer)
    private Integer number;

    @Override
    public String generateContent() {
        return name;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(name)) {
            throw exception(MATERIAL_FIELD_NOT_VALID,"合同名称不能为空");
        }
    }
}
