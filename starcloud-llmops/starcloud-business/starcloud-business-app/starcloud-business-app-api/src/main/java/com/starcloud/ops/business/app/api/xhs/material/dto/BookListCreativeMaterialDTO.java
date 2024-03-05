package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;
import org.apache.tika.utils.StringUtils;

@Data
public class BookListCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO{

    @FieldDefine(fieldName = "bookName", desc = "书名", type = FieldTypeEnum.string)
    private String bookName;

    @FieldDefine(fieldName = "coverUrl", desc = "封面", type = FieldTypeEnum.image)
    private String coverUrl;

    @FieldDefine(fieldName = "introduction", desc = "简介", type = FieldTypeEnum.string)
    private String introduction;

    @FieldDefine(fieldName = "author", desc = "作者", type = FieldTypeEnum.string)
    private String author;

    @FieldDefine(fieldName = "rating", desc = "评分", type = FieldTypeEnum.number)
    private Double rating;

    @FieldDefine(fieldName = "senseAfterReading", desc = "读后感", type = FieldTypeEnum.string)
    private String senseAfterReading;

    @FieldDefine(fieldName = "commentsOne", desc = "评论1", type = FieldTypeEnum.string)
    private String commentsOne;

    @FieldDefine(fieldName = "commentsTwo", desc = "评论2", type = FieldTypeEnum.string)
    private String commentsTwo;

    @FieldDefine(fieldName = "commentsThree", desc = "评论3", type = FieldTypeEnum.string)
    private String commentsThree;

    @Override
    String generateContent() {
        return bookName;
    }
}
