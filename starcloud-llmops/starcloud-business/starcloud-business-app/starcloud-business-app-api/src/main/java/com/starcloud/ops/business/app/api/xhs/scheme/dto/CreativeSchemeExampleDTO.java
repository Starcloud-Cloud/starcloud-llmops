package com.starcloud.ops.business.app.api.xhs.scheme.dto;

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
@Schema(name = "CreativeExampleDTO", description = "创作计划示例")
public class CreativeSchemeExampleDTO implements java.io.Serializable {

    private static final long serialVersionUID = 946559535552588387L;

    /**
     * 文案示例
     */
    @Schema(description = "文案示例")
    private CopyWritingContentDTO copyWriting;

    /**
     * 图片示例
     */
    @Schema(description = "图片示例")
    private List<CreativeImageDTO> images;
}
