package com.starcloud.ops.llm.langchain.core.memory;

import com.starcloud.ops.llm.langchain.core.model.chat.base.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseChatMemory extends BaseMemory {

    private Boolean returnMessages = false;

    protected static final String MEMORY_KEY = "history";

    protected static final String INPUT_KEY = "input";

    private ChatMessageHistory chatHistory;

    public BaseChatMemory() {
        this.setChatHistory(new ChatMessageHistory());
    }


    public String getBufferString(List<BaseChatMessage> messages) {
        return Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map(message -> {

            String role = "Human";
            if (message instanceof HumanMessage) {
                role = "Human";
            } else if (message instanceof AIMessage) {
                role = "AI";
            } else if (message instanceof SystemMessage) {
                role = "System";
            } else {
                role = "Human";
            }
            return role + ": " + message.getContent();
        }).collect(Collectors.joining("\n"));
    }

    protected BaseVariable getPromptInputKey(List<BaseVariable> baseVariables) {

        return Optional.ofNullable(baseVariables).orElse(new ArrayList<>()).stream().filter(variable -> INPUT_KEY.equals(variable.getField())).findFirst().get();
    }


    @Override
    public void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result) {

        BaseVariable variable = getPromptInputKey(baseVariables);
        getChatHistory().addUserMessage(String.valueOf(variable.getValue()));
        getChatHistory().addAiMessage(result.getText());
    }

}
