package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "聊天模型参数配置")
@Builder
public class SpeakConfigRespVO {

    @Schema(description = "开启语音")
    private Boolean enabled;

    /**
     * 模型
     */
    @Schema(description = "语音模型")
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
     * 基线音节
     * 50%
     */
    @Schema(description = "基线音节")
    private String prosodyPitch;

    /**
     * 音量
     * +20.00%
     */
    @Schema(description = "音量")
    private String prosodyVolume;
}
