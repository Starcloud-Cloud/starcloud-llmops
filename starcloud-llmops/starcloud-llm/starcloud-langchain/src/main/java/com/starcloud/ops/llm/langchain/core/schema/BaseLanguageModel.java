package com.starcloud.ops.llm.langchain.core.schema;

import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
@Data
public abstract class BaseLanguageModel<R> {

    public abstract BaseLLMResult<R> generatePrompt(List<PromptValue> promptValues);

    public Integer getNumTokens() {
        return 0;
    }

    public List<BaseChatMessage> getNumTokensFromMessages() {
        return null;
    }

    public abstract void setVerbose(Boolean verbose);

}

