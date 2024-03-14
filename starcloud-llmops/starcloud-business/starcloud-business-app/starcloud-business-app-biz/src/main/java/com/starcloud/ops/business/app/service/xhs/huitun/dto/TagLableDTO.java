package com.starcloud.ops.business.app.service.xhs.huitun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class TagLableDTO {

    /**
     * 标签 ID
     */
    private Integer tagId;
    /**
     * 标签
     */
    private String label;
    /**
     * 下级标签
     */
    private List<TagLableDTO> subTagList;

}
