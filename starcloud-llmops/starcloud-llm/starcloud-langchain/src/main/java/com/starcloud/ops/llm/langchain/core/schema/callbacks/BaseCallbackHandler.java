package com.starcloud.ops.llm.langchain.core.schema.callbacks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class BaseCallbackHandler {

    public Boolean ignoreLLM() {
        return false;
    }

    public Boolean ignoreChain() {
        return false;
    }

    public void onLLMStart(Object... objects) {
    }

    public void onLLMNewToken(Object... objects) {
    }

    public void onLLMEnd(Object... objects) {
    }


    public void onLLMError(String message) {
    }

    public void onLLMError(String message, Throwable throwable) {
    }


}
