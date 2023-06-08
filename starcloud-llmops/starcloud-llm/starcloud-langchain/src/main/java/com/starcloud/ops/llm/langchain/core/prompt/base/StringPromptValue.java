package com.starcloud.ops.llm.langchain.core.prompt.base;


import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.HumanMessage;
import lombok.Data;

import java.util.Arrays;
import java.util.List;


@Data
public class StringPromptValue extends PromptValue {

    private String str;

    public StringPromptValue(String str) {
        this.str = str;
    }

    @Override
    public String toStr() {
        return this.str;
    }

    @Override
    public List<BaseChatMessage> toMessage() {
        return Arrays.asList(HumanMessage.builder().content(this.str).build());
    }
}
