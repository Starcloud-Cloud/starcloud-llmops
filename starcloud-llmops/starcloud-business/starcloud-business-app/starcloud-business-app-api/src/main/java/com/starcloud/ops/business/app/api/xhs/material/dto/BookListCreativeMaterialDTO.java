package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = false)
public class BookListCreativeMaterialDTO extends AbstractCreativeMaterialDTO {

    private static final long serialVersionUID = -7802198500802130936L;

    @ExcelProperty("书名")
    @JsonPropertyDescription("书名")
    @FieldDefine(desc = "书名", type = FieldTypeEnum.string, required = true)
    private String bookName;

    @ExcelProperty("封面")
    @JsonPropertyDescription("封面")
    @FieldDefine(desc = "封面", type = FieldTypeEnum.image, required = true)
    private String coverUrl;

    @ExcelProperty("简介")
    @JsonPropertyDescription("简介")
    @FieldDefine(desc = "简介", type = FieldTypeEnum.textBox, required = true)
    private String introduction;

    @ExcelProperty("作者")
    @JsonPropertyDescription("作者")
    @FieldDefine(desc = "作者", type = FieldTypeEnum.string, required = true)
    private String author;

    @ExcelProperty("评分")
    @JsonPropertyDescription("评分")
    @FieldDefine(desc = "评分", type = FieldTypeEnum.string)
    private String rating;

    @ExcelProperty("读后感")
    @JsonPropertyDescription("读后感")
    @FieldDefine(desc = "读后感", type = FieldTypeEnum.textBox)
    private String senseAfterReading;

    @ExcelProperty("评论1")
    @JsonPropertyDescription("评论1")
    @FieldDefine(desc = "评论1", type = FieldTypeEnum.textBox)
    private String commentsOne;

    @ExcelProperty("评论2")
    @JsonPropertyDescription("评论2")
    @FieldDefine(desc = "评论2", type = FieldTypeEnum.textBox)
    private String commentsTwo;

    @ExcelProperty("评论3")
    @JsonPropertyDescription("评论3")
    @FieldDefine(desc = "评论3", type = FieldTypeEnum.textBox)
    private String commentsThree;

    @Override
    public String generateContent() {
        return bookName;
    }

}
