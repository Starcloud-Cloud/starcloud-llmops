package com.starcloud.ops.business.app.domain.entity.chat;

import lombok.Data;

@Data
public class SpeakConfigEntity {

    private Boolean enabled;

    /**
     * 模型
     */
    private String shortName;

    /**
     * 语言
     */
    private String locale;

    /**
     * 讲话角色
     */
    private String role;

    /**
     * 语音风格
     */
    private String style;

    /**
     * 语速
     * +30.00%
     */
    private String prosodyRate;

    /**
     * 基线音节
     * 50%
     */
    private String prosodyPitch;

    /**
     * 音量
     * +20.00%
     */
    private String prosodyVolume;

}
