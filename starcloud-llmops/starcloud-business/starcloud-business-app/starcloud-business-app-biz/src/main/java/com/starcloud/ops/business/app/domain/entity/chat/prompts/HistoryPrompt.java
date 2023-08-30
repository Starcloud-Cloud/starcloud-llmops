package com.starcloud.ops.business.app.domain.entity.chat.prompts;


import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * 聊天历史 prompt
 */
@Slf4j
@Data
public class HistoryPrompt extends BasePromptConfig {


    private String promptV1 = "Here is the history of the dialogue\n\n{history}";


    private BaseChatMemory chatMemory;

    public HistoryPrompt(BaseChatMemory chatMemory) {

        this.chatMemory = chatMemory;
    }


    @Override
    protected PromptTemplate _buildPrompt() {
        return new PromptTemplate(this.promptV1);
    }


    @Override
    protected Boolean _isEnable() {
        return this.chatMemory.getChatHistory() != null && this.chatMemory.getChatHistory().limitMessage(1).size() > 0;
    }

}
