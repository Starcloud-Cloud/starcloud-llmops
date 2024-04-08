package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = false)
public class ContractCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    private static final long serialVersionUID = -3928689367907211655L;

    @JsonPropertyDescription("合同名称")
    @FieldDefine(desc = "合同名称", type = FieldTypeEnum.string, required = true)
    @ExcelProperty("合同名称")
    private String name;

    @JsonPropertyDescription("合同编号")
    @FieldDefine(desc = "合同编号", type = FieldTypeEnum.string)
    @ExcelProperty("合同编号")
    private String numbering;

    @JsonPropertyDescription("合同简介")
    @FieldDefine(desc = "合同简介", type = FieldTypeEnum.string)
    @ExcelProperty("合同简介")
    private String desc;

    @JsonPropertyDescription("文档图片1")
    @FieldDefine(desc = "文档图片1", type = FieldTypeEnum.image)
    @ExcelProperty("文档图片1")
    private String documentPicUrlOne;

    @JsonPropertyDescription("文档图片2")
    @FieldDefine(desc = "文档图片2", type = FieldTypeEnum.image)
    @ExcelProperty("文档图片2")
    private String documentPicUrlTwo;

    @JsonPropertyDescription("文档图片3")
    @FieldDefine(desc = "文档图片3", type = FieldTypeEnum.image)
    @ExcelProperty("文档图片3")
    private String documentPicUrlThree;

    @JsonPropertyDescription("文档图片4")
    @FieldDefine(desc = "文档图片4", type = FieldTypeEnum.image)
    @ExcelProperty("文档图片4")
    private String documentPicUrlFour;

    @JsonPropertyDescription("文档图片5")
    @FieldDefine(desc = "文档图片5", type = FieldTypeEnum.image)
    @ExcelProperty("文档图片5")
    private String documentPicUrlFive;

    @JsonPropertyDescription("文档图片6")
    @FieldDefine(desc = "文档图片6", type = FieldTypeEnum.image)
    @ExcelProperty("文档图片6")
    private String documentPicUrlSix;

    @ExcelProperty("文档相对地址")
    private String docRelativeAddr;

    @Override
    public String generateContent() {
        return name;
    }

}
