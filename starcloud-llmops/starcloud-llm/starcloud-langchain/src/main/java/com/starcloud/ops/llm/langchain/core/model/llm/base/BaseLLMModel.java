package com.starcloud.ops.llm.langchain.core.model.llm.base;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;

import java.util.*;
import java.util.stream.Collectors;


public abstract class BaseLLMModel<P, R> {

    private LLMCallbackManager callbackManager;

    public LLMCallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setCallbackManager(LLMCallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    /**
     * @param params
     * @return
     */
    public abstract BaseLLMResult<R> call(P params);

    public abstract BaseLLMResult<R> generatePrompt(PromptValue promptValue);

    public List<BaseLLMResult<R>> generate(Collection<P> pCollection) {
        return Optional.ofNullable(pCollection).orElse(new ArrayList<>()).stream().map(params -> {
            return this.call(params);
        }).collect(Collectors.toList());
    }


    public Long getNumTokens(String text) {

        return (long) (StrUtil.length(text) / 4);
    }


    public Long getNumTokensFromMessages(List<BaseChatMessage> messages) {

        Long sum = Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map((message) -> {
            message.setTokens(this.getNumTokens(message.getContent()));
            return message.getTokens();
        }).reduce(0L, Long::sum);

        return sum;
    }


}
