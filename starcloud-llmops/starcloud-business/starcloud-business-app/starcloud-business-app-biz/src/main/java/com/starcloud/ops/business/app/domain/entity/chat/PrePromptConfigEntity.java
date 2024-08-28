package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.business.app.domain.entity.config.BaseConfigEntity;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.api.verification.Verification;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

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

    @Override
    public List<Verification> validate(String uid, ValidateTypeEnum validateType) {
        return Collections.emptyList();
    }
}
