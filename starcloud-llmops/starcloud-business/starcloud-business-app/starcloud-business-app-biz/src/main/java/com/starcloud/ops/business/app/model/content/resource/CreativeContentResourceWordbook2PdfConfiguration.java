package com.starcloud.ops.business.app.model.content.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Schema(description = "创作内容单词pdf资源配置")
@Data
public class CreativeContentResourceWordbook2PdfConfiguration implements Serializable {

    private static final long serialVersionUID = -144402176424682921L;

    private String wordField;

    private String paraphraseField;

    private String posterId;

}
