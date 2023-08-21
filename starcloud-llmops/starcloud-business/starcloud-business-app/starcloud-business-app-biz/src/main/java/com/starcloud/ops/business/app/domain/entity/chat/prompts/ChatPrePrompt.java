package com.starcloud.ops.business.app.domain.entity.chat.prompts;

import lombok.Data;

/**
 * 聊天的 前置初始化 prompt
 */
@Data
public class ChatPrePrompt extends BasePromptConfig {

    //@todo 可以再增加 系统 prompt
    private String promptV1 = "";

    public ChatPrePrompt(String promptV1) {
        this.promptV1 = promptV1;
    }


    @Override
    public void validate() {

    }

    @Override
    protected String _buildPromptStr() {
        return this.promptV1;
    }

    @Override
    protected Boolean _isEnable() {
        return true;
    }
}
