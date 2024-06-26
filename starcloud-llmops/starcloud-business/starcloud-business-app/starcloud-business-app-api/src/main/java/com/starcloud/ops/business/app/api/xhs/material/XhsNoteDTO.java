package com.starcloud.ops.business.app.api.xhs.material;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class XhsNoteDTO {

    @Schema(description = "笔记id")
    private String noteId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "图片集合")
    private List<String> imageList;


    public void addImage(String url) {
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
        imageList.add(url);
    }
}
