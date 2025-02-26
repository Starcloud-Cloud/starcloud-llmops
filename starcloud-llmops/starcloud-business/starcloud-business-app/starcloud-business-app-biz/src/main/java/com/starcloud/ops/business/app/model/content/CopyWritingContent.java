package com.starcloud.ops.business.app.model.content;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "文案内容对象")
public class CopyWritingContent implements java.io.Serializable {

    private static final long serialVersionUID = -8566370798934412863L;

    /**
     * 文案标题
     */
    @Schema(description = "笔记标题")
    @JsonPropertyDescription("标题")
    private String title;

    /**
     * 文案内容
     */
    @Schema(description = "笔记内容")
    @JsonPropertyDescription("内容")
    private String content;

    /**
     * 标签列表
     */
    @Schema(description = "标签列表")
    @JsonPropertyDescription("标签")
    private List<String> tagList;

}
