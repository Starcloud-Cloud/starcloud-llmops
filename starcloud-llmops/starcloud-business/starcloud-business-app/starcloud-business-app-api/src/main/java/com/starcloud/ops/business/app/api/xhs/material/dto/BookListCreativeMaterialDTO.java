package com.starcloud.ops.business.app.api.xhs.material.dto;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
@Accessors(chain = false)
public class BookListCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO {

    @ExcelProperty("书名")
    @JsonPropertyDescription("书名")
    @FieldDefine(desc = "书名", type = FieldTypeEnum.string)
    private String bookName;

    @ExcelProperty("封面")
    @JsonPropertyDescription("封面")
    @FieldDefine(desc = "封面", type = FieldTypeEnum.image)
    private String coverUrl;

    @ExcelProperty("简介")
    @JsonPropertyDescription("简介")
    @FieldDefine(desc = "简介", type = FieldTypeEnum.string)
    private String introduction;

    @ExcelProperty("作者")
    @JsonPropertyDescription("作者")
    @FieldDefine(desc = "作者", type = FieldTypeEnum.string)
    private String author;

    @ExcelProperty("评分")
    @JsonPropertyDescription("评分")
    @FieldDefine(desc = "评分", type = FieldTypeEnum.decimal)
    private Double rating;

    @ExcelProperty("读后感")
    @JsonPropertyDescription("读后感")
    @FieldDefine(desc = "读后感", type = FieldTypeEnum.string)
    private String senseAfterReading;

    @ExcelProperty("评论1")
    @JsonPropertyDescription("评论1")
    @FieldDefine(desc = "评论1", type = FieldTypeEnum.string)
    private String commentsOne;

    @ExcelProperty("评论2")
    @JsonPropertyDescription("评论2")
    @FieldDefine(desc = "评论2", type = FieldTypeEnum.string)
    private String commentsTwo;

    @ExcelProperty("评论3")
    @JsonPropertyDescription("评论3")
    @FieldDefine(desc = "评论3", type = FieldTypeEnum.string)
    private String commentsThree;

    @Override
    public String generateContent() {
        return bookName;
    }

    @Override
    public void valid() {
        if (StrUtil.isBlank(bookName)) {
            throw exception(MATERIAL_FIELD_NOT_VALID,"书名不能为空");
        }
    }
}