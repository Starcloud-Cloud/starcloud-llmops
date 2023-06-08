package com.starcloud.ops.llm.langchain.core.prompt.base;


import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
public class ChatPromptValue extends PromptValue {


    private List<BaseChatMessage> messages;

    public ChatPromptValue(List<BaseChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toStr() {
        return this.messages.get(0).getContent();
    }

    @Override
    public List<BaseChatMessage> toMessage() {
        return this.messages;
    }
}
