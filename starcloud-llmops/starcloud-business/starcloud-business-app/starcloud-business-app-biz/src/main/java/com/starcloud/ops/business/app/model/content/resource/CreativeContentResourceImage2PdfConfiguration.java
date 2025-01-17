package com.starcloud.ops.business.app.model.content.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创作内容图片资源配置
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@Schema(description = "创作内容图片资源配置")
public class CreativeContentResourceImage2PdfConfiguration implements Serializable {

    private static final long serialVersionUID = 3139920323142676962L;

    private String title;

    private String subTitle;

    private List<String> imageUrlList;

    /**
     * 是否添加图片二维码
     */
    @Schema(description = "是否添加图片二维码")
    private Boolean isAddVideoQrCode;

    /**
     * 是否添加音频二维码
     */
    @Schema(description = "是否添加音频二维码")
    private Boolean isAddAudioQrCode;

    /**
     * 二维码位置
     */
    @Schema(description = "二维码位置")
    private String qrCodeLocation;

}
