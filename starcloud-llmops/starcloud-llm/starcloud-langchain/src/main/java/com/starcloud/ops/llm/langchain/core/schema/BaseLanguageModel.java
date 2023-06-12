package com.starcloud.ops.llm.langchain.core.schema;

import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
@Data
public abstract class BaseLanguageModel<R> {

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

}

