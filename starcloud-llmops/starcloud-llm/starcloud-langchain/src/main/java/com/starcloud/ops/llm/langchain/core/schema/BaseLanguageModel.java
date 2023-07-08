package com.starcloud.ops.llm.langchain.core.schema;

import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.LLMCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
@Data
public abstract class BaseLanguageModel<R, C extends BaseCallbackManager> {

    public abstract BaseLLMResult<R> generatePrompt(List<PromptValue> promptValues);

    public Long getNumTokens(String text) {
        return TokenUtils.tokens(ModelType.GPT_3_5_TURBO, text);
    }

    public Long getNumTokensFromMessages(List<BaseChatMessage> messages) {
        Long sum = Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map((message) -> {
            message.setTokens(this.getNumTokens(message.getContent()));
            return message.getTokens();
        }).reduce(0L, Long::sum);

        return sum;
    }

    public abstract void setVerbose(Boolean verbose);

    public abstract String predict(String text, List<String> stops);

    public abstract BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops);

    public abstract BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops, C callbackManager);

}

