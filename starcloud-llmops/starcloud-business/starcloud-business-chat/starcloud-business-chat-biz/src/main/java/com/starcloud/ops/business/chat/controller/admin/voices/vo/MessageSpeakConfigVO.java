package com.starcloud.ops.business.chat.controller.admin.voices.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * Azure 语音模型可控参数
 */
@Data
public class MessageSpeakConfigVO extends SpeakConfigVO {


    /**
     * 模型
     */
    private String messageUid;


    /**
     * 内容
     */
    private String text;

}
