package com.starcloud.ops.llm.langchain.core.memory.buffer;

import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

/**
 * @author df007df
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConversationBufferMemory extends BaseChatMemory {

    @Override
    public List<BaseVariable> loadMemoryVariables() {

        List<BaseChatMessage> messages = getChatHistory().getMessages();
        return Collections.singletonList(BaseVariable.builder()
                .field(MEMORY_KEY)
                .value(BaseChatMessage.getBufferString(messages))
                .build());
    }


}
