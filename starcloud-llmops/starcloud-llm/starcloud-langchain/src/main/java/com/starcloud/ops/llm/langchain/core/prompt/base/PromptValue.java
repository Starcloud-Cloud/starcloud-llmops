package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

public abstract class PromptValue {

    public abstract String toStr();

    public abstract List<BaseChatMessage> toMessage();

}
