package com.starcloud.ops.business.app.controller.admin.chat.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * Azure 语音模型可控参数
 */
@Data
public class SpeakConfigVO implements Serializable {


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
