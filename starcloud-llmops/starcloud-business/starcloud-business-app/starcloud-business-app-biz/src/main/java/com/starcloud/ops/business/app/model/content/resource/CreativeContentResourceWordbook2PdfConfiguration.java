package com.starcloud.ops.business.app.model.content.resource;

import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@Schema(description = "创作内容单词pdf资源配置")
public class CreativeContentResourceWordbook2PdfConfiguration implements Serializable {

    private static final long serialVersionUID = -144402176424682921L;

    /**
     * 单词字段
     */
    @Schema(description = "单词字段")
    @NotBlank(message = "单词字段不能为空")
    private String wordField;

    /**
     * 释义字段
     */
    @NotBlank(message = "释义字段不能为空")
    private String paraphraseField;

    /**
     * 海报模板ID
     */
    @NotBlank(message = "海报模板不能为空")
    private PosterTemplateDTO posterTemplate;

}
