package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.business.app.domain.entity.config.BaseConfigEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天应用配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
@EqualsAndHashCode
public class PrePromptConfigEntity extends BaseConfigEntity {


    /**
     * 用户输入 prompt
     */
    private String prePrompt;


    /**
     * 回复语气
     */
    private String tone;

    /**
     * 回复的语言
     */
    private String replyLang;

    /**
     * 最大回复多少个字
     */
    private Integer maxReturn;


    /**
     * 校验实体，对一写复杂逻辑的校验，可以在这里实现
     */
    @Override
    public void validate() {

    }
}
