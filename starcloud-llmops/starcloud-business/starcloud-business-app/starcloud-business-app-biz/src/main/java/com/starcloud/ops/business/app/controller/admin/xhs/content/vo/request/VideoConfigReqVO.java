package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import com.starcloud.ops.business.app.model.content.VideoContent;
import com.starcloud.ops.business.app.model.content.VideoContentInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "图片视频生成配置")
public class VideoConfigReqVO implements Serializable {

    private static final long serialVersionUID = 4035060399572428688L;

    @Schema(description = "创作内容uid")
    @NotBlank(message = "创作内容uid必填")
    private String uid;

    @Schema(description = "快捷配置")
    @NotBlank(message = "快捷配置必填")
    private String quickConfiguration;

    @Schema(description = "生成视频配置")
    private String videoConfig;

    @Schema(description = "图片模板code")
    private String imageCode;

    @Schema(description = "视频")
    private VideoContentInfo video;

    @Schema(description = "图片地址")
    private String imageUrl;

}
