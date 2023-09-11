package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * llm标识
 */
public enum ModelProviderEnum implements IEnumable<Integer> {

    GPT35(1, "大语言模型3.5", ""),

    GPT4(2, "大语言模型4.0", "chat:config:llm:gpt4"),

    QWEN(3, "千问大语言模型", "chat:config:llm:qwen");

    private Integer code;

    private String label;

    @Getter
    private String permissions;

    ModelProviderEnum(Integer code, String label, String permissions) {
        this.code = code;
        this.label = label;
        this.permissions = permissions;
    }


    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}