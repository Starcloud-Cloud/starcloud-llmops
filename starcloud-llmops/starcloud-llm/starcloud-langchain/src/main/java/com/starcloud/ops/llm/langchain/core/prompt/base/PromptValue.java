package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
public class PromptValue {

    private String str;

    private List<BaseChatMessage> messages;

}
