package com.starcloud.ops.business.app.model.content;

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
@Schema(description = "创作内容执行结果")
public class CreativeContentExecuteResult implements java.io.Serializable {

    private static final long serialVersionUID = -8427636659572638023L;

    /**
     * 文案
     */
    @Schema(description = "文案")
    private CopyWritingContent copyWriting;

    /**
     * 图片
     */
    @Schema(description = "图片")
    private List<ImageContent> imageList;

    /**
     * 视频
     */
    @Schema(description = "视频")
    private VideoContentInfo video;

}
