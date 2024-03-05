package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import lombok.Data;

@Data
public class BookListCreativeMaterialDTO extends AbstractBaseCreativeMaterialDTO{

    @FieldDefine(desc = "书名", type = FieldTypeEnum.string)
    private String bookName;

    @FieldDefine(desc = "封面", type = FieldTypeEnum.image)
    private String coverUrl;

    @FieldDefine(desc = "简介", type = FieldTypeEnum.string)
    private String introduction;

    @FieldDefine(desc = "作者", type = FieldTypeEnum.string)
    private String author;

    @FieldDefine(desc = "评分", type = FieldTypeEnum.number)
    private Double rating;

    @FieldDefine(desc = "读后感", type = FieldTypeEnum.string)
    private String senseAfterReading;

    @FieldDefine(desc = "评论1", type = FieldTypeEnum.string)
    private String commentsOne;

    @FieldDefine(desc = "评论2", type = FieldTypeEnum.string)
    private String commentsTwo;

    @FieldDefine(desc = "评论3", type = FieldTypeEnum.string)
    private String commentsThree;

    @Override
    String generateContent() {
        return bookName;
    }
}
