package com.starcloud.ops.business.app.api.app.vo.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "语音配置")
@NoArgsConstructor
public class AudioConfigReqVO {

    @Schema(description = "开启")
    private Boolean enabled;

    /**
     * 模型
     */
    @Schema(description = "模型")
    private String shortName;

    /**
     * 语言
     */
    @Schema(description = "语言")
    private String locale;

    /**
     * 讲话角色
     */
    @Schema(description = "讲话角色")
    private String role;

    /**
     * 语音风格
     */
    @Schema(description = "语音风格")
    private String style;

    /**
     * 语速
     * +30.00%
     */
    @Schema(description = "语速")
    private String prosodyRate;

    /**
     * 基线语调
     * 50%
     */
    @Schema(description = "基线语调")
    private String prosodyPitch;

    /**
     * 音量
     * +20.00%
     */
    @Schema(description = "音量")
    private String prosodyVolume;
}
