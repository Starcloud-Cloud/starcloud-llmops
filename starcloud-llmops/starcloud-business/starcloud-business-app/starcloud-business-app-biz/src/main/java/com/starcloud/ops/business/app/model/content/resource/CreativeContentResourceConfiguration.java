package com.starcloud.ops.business.app.model.content.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 创作内容资源配置
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@Schema(description = "创作内容资源配置")
public class CreativeContentResourceConfiguration implements Serializable {

    private static final long serialVersionUID = 5246155253215539440L;

    /**
     * 图片配置
     */
    @Schema(description = "图片生成PDF配置")
    private CreativeContentResourceImage2PdfConfiguration imagePdfConfiguration;

    /**
     * 单词本生成pdf配置
     */
    @Schema(description = "单词本生成pdf配置")
    private CreativeContentResourceWordbook2PdfConfiguration wordbookPdfConfiguration;
}
