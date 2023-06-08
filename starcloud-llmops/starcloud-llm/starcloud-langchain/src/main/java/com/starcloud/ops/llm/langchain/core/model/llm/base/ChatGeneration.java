package com.starcloud.ops.llm.langchain.core.model.llm.base;

import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatGeneration<R> extends BaseGeneration<R> {

    private BaseChatMessage chatMessage;

    private BaseLLMUsage usage;

    @Override
    public String getText() {
        return chatMessage.getContent();
    }
}
